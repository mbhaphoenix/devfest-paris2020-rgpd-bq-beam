package com.mbhaphoenix.gcp.aead.ptransforms;

import com.google.api.services.bigquery.model.TableRow;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.windowing.AfterProcessingTime;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.transforms.windowing.Repeatedly;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;

public class WindowingAndGrouping extends PTransform<PCollection<KV<String, TableRow>>, PCollection<KV<String, Iterable<TableRow>>>> {

    @Override
    public PCollection<KV<String, Iterable<TableRow>>> expand(PCollection<KV<String, TableRow>> kvs) {
        return  kvs.apply("Windowing",
                Window.<KV<String, TableRow>>into(new GlobalWindows())
                        .triggering(Repeatedly.forever(
                                AfterProcessingTime.pastFirstElementInPane().alignedTo(Duration.standardMinutes(1))))
                        .discardingFiredPanes()
        )
                .apply("Grouping by key", GroupByKey.<String, TableRow>create());
    }
}
