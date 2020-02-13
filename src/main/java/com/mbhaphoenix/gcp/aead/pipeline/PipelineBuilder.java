package com.mbhaphoenix.gcp.aead.pipeline;

import com.google.api.services.bigquery.model.TableRow;
import com.google.cloud.bigtable.beam.CloudBigtableIO;
import com.google.cloud.bigtable.beam.CloudBigtableTableConfiguration;
import com.mbhaphoenix.gcp.aead.dofns.EncryptionDoFn;
import com.mbhaphoenix.gcp.aead.job.JobOptions;
import com.mbhaphoenix.gcp.aead.ptransforms.PubSubStringsToKVTableRows;
import com.mbhaphoenix.gcp.aead.ptransforms.WindowingAndGrouping;
import com.mbhaphoenix.gcp.aead.utils.Schemas;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.apache.hadoop.hbase.client.Mutation;

public class PipelineBuilder {

    public static Pipeline buildPipeline(JobOptions options)  {

        Pipeline pipeline = Pipeline.create(options);

        TupleTag<TableRow> encryptedRecordTupleTag = new TupleTag<TableRow>() {};
        TupleTag<Mutation> keysetTupleTag = new TupleTag<Mutation>() {};

        PCollectionTuple pCollectionTuple  = pipeline
                .apply("Reading PubSub records into KVs", new PubSubStringsToKVTableRows())
                .apply("Windoing and grouping", new WindowingAndGrouping())
                .apply("Applying AEAD", ParDo.of(new EncryptionDoFn(encryptedRecordTupleTag, keysetTupleTag))
                        .withOutputTags(encryptedRecordTupleTag, TupleTagList.of(keysetTupleTag)));


        pCollectionTuple.get(keysetTupleTag)
                .apply("Bigtable writes of new keysets mutations",
                        CloudBigtableIO
                                .writeToTable(new CloudBigtableTableConfiguration.Builder()
                                .withProjectId(options.getProject())
                                .withInstanceId(options.getInstance())
                                .withTableId(options.getKeysetsTable())
                                .build()
                                )
                );


        pCollectionTuple.get(encryptedRecordTupleTag)
                .apply("BigQuery streaming writes of encrypted tableRows",
                        BigQueryIO
                                .writeTableRows()
                                .to(options.getOutputTable())
                                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
                                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                                .withSchema(Schemas.ENCRYPTED_TABLE_SCHEMA)
                                .withMethod(BigQueryIO.Write.Method.STREAMING_INSERTS)
                );


        return pipeline;
    }


}
