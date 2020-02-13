package com.mbhaphoenix.gcp.aead.job;

import com.mbhaphoenix.gcp.aead.pipeline.PipelineBuilder;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

public class Driver {

    public static void main(String[] args) {

            JobOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(JobOptions.class);
            Pipeline pipeline = PipelineBuilder.buildPipeline(options);
            pipeline.run();

    }
}
