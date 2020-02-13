package com.mbhaphoenix.gcp.aead.dofns;

import com.google.api.services.bigquery.model.TableRow;
import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.mbhaphoenix.gcp.aead.job.JobOptions;
import com.mbhaphoenix.gcp.aead.utils.Schemas;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class EncryptionDoFn extends DoFn<KV<String, Iterable<TableRow>>, TableRow> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionDoFn.class);

    private Connection connection;
    private Table keysetsTable;

    private final TupleTag<TableRow> encryptedRecordTupleTag;
    private final TupleTag<Mutation> keysetTupleTag;


    public EncryptionDoFn(TupleTag<TableRow> encryptedRecordTupleTag, TupleTag<Mutation> keysetTupleTag) {
        this.encryptedRecordTupleTag = encryptedRecordTupleTag;
        this.keysetTupleTag = keysetTupleTag;
    }


    @Setup
    public void init() throws Exception {
        LOGGER.info("[SETUP] AeadConfig registration");
        AeadConfig.register();
    }

    @StartBundle
    public void startBundle(StartBundleContext startBundleContext) {
        JobOptions options = startBundleContext.getPipelineOptions().as(JobOptions.class);
        connection = BigtableConfiguration.connect(options.getProject(), options.getInstance());
        LOGGER.info("[BUNDLE][BIGTABLE][CONNECTION][CREATION]");
        try {
            keysetsTable = connection.getTable(TableName.valueOf(options.getKeysetsTable()));
        } catch (IOException e) {
            LOGGER.error("[BUNDLE][BIGTABLE][TABLE][NAME={}]", options.getKeysetsTable());
        }
    }

    @ProcessElement
    public void processElement(
            ProcessContext processContext,
            MultiOutputReceiver out) throws Exception {

        JobOptions options = processContext.getPipelineOptions().as(JobOptions.class);

        String id = processContext.element().getKey();
        KeysetHandle keysetHandle;

        byte[] keysetBytes = getKeysetBytesFromBigTable(options, id);

        if (keysetBytes == null) {
            LOGGER.debug("[PROCESS][KEYSET_HANDLE][NEW][id={}]", id);
            keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES256_GCM);
            ByteArrayOutputStream baops = new ByteArrayOutputStream();
            CleartextKeysetHandle.write(keysetHandle, BinaryKeysetWriter.withOutputStream(baops));
            keysetBytes = baops.toByteArray();

            LOGGER.info("[BIGTABLE][WRITE][ROWKEY][id={}]", id);
            Put put = new Put(Bytes.toBytes(id)).addColumn(Bytes.toBytes(options.getFamily()), Bytes.toBytes(Schemas.KEYSET_COLUMN_NAME), keysetBytes);
            out.get(keysetTupleTag).output(put);

        } else {
            LOGGER.debug("[PROCESS][KEYSET_HANDLE][EXISTING_KEYSET][id={}]", id);
            keysetHandle = CleartextKeysetHandle
                    .read(BinaryKeysetReader.withBytes(keysetBytes));
        }

        final Aead aead = keysetHandle.getPrimitive(Aead.class);

        // AEAD Encryption
        for (TableRow clearTableRow : processContext.element().getValue()) {
            out.get(encryptedRecordTupleTag).output(buildEncryptedTableRow(id, aead, clearTableRow));
        }
    }

    private byte[] getKeysetBytesFromBigTable(JobOptions options, String id) throws IOException {
        return keysetsTable.get(
                new Get(Bytes.toBytes(id)).addColumn(Bytes.toBytes(options.getFamily()), Bytes.toBytes(Schemas.KEYSET_COLUMN_NAME))
        ).getValue(
                Bytes.toBytes(options.getFamily()), Bytes.toBytes(Schemas.KEYSET_COLUMN_NAME)
        );
    }

    private TableRow buildEncryptedTableRow(String id, Aead aead, TableRow clearTableRow) throws GeneralSecurityException {
        TableRow encryptedTableRow = clearTableRow.clone();

        encryptedTableRow.remove(Schemas.INDICATOR_NAME_COLUMN_NAME);

        encryptedTableRow.set(
                Schemas.CIPHER_INDICATOR_NAME_COLUMN_NAME, aead.encrypt(
                        ((String) clearTableRow.get(Schemas.INDICATOR_NAME_COLUMN_NAME)).getBytes(),
                        id.getBytes(StandardCharsets.UTF_8)
                )
        );
        return encryptedTableRow;
    }

    @FinishBundle
    public void finishBundle() {
        try {
            connection.close();
            LOGGER.debug("[FINISH_BUNDLE][BIGTABLE][CONNECTION][CLOSE]");
        } catch (IOException e) {
            LOGGER.error("[FINISH_BUNDLE][BIGTABLE][CONNECTION][CLOSE]", e);
        }
    }


}
