package com.mbhaphoenix.gcp.aead.ptransforms;

import com.google.api.services.bigquery.model.TableRow;
import com.mbhaphoenix.gcp.aead.job.JobOptions;
import com.mbhaphoenix.gcp.aead.utils.Schemas;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.io.gcp.bigquery.TableRowJsonCoder;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PubSubStringsToKVTableRows extends PTransform<PBegin, PCollection<KV<String, TableRow>>> {


    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubStringsToKVTableRows.class);

    @Override
    public PCollection<KV<String, TableRow>> expand(PBegin pBegin) {

        JobOptions options = (JobOptions) pBegin.getPipeline().getOptions();

        return pBegin.getPipeline()
                .apply("Reading JSON string records From PubSub subscription",
                        PubsubIO.readStrings().fromSubscription(
                                "projects/" + options.getProject() + "/subscriptions/" + options.getSubscription()
                        )

                )
                .apply("Deserializing JSON Strings into tableRows", MapElements.via(
                        new SimpleFunction<String, TableRow>() {
                            @Override
                            public TableRow apply(String jsonString) {
                                LOGGER.debug("json string {}", jsonString);
                                // Parse the JSON into a {@link TableRow} object.
                                try (InputStream inputStream =
                                             new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8))) {
                                    return TableRowJsonCoder.of().decode(inputStream, Coder.Context.OUTER);
                                } catch (IOException e) {
                                    throw new RuntimeException("Failed to deserialize json string to tableRow: " + jsonString, e);
                                }
                            }
                        }))
                .apply("Mapping tableRows to KV<id, tableRow> ",
                        MapElements.into(TypeDescriptors.kvs(TypeDescriptors.strings(), TypeDescriptor.of(TableRow.class)))
                                .via((TableRow tableRow) -> KV.of((String) tableRow.get(Schemas.ID_COLUMN_NAME), tableRow)));
    }
}
