package com.mbhaphoenix.gcp.aead.job;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.options.ValueProvider;

public interface JobOptions extends DataflowPipelineOptions {

    @Description("PubSub subscription to read data from")
    @Default.String("aead-streaming")
    @Validation.Required
    String getSubscription();

    void setSubscription(String value);

    @Description("Encrypted data output table")
    @Validation.Required
    String getOutputTable();

    void setOutputTable(String value);


    @Description("Bigtable instance")
    @Validation.Required
    String getInstance();

    void setInstance(String value);

    @Description("BigTable Keysets table")
    @Validation.Required
    String getKeysetsTable();

    void setKeysetsTable(String value);

    @Description("keysets column family")
    @Default.String("cf")
    String getFamily();

    void setFamily(String value);



}
