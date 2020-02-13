package com.mbhaphoenix.gcp.aead.utils;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.common.collect.ImmutableList;


public interface Schemas {

    String KEYSET_COLUMN_NAME = "keyset";

    String STRING_TYPE_NAME = "STRING";
    String INTEGER_TYPE_NAME = "INTEGER";
    String FLOAT_TYPE_NAME = "FLOAT";
    String BYTES_TYPE_NAME = "BYTES";


    String COUNTRY_NAME_COLUMN_NAME = "country_name";
    String COUNTRY_CODE_COLUMN_NAME = "country_code";
    String INDICATOR_NAME_COLUMN_NAME = "indicator_name";
    String CIPHER_INDICATOR_NAME_COLUMN_NAME = "cipher_indicator_name";
    String INDICATOR_CODE_COLUMN_NAME = "indicator_code";
    String VALUE_COLUMN_NAME = "value";
    String YEAR_COLUMN_NAME = "year";


    String ID_COLUMN_NAME = "indicator_code";

    TableSchema ENCRYPTED_TABLE_SCHEMA = new TableSchema().setFields(
            ImmutableList.of(
                    new TableFieldSchema().setName(COUNTRY_NAME_COLUMN_NAME).setType(STRING_TYPE_NAME),
                    new TableFieldSchema().setName(COUNTRY_CODE_COLUMN_NAME).setType(STRING_TYPE_NAME),
                    new TableFieldSchema().setName(CIPHER_INDICATOR_NAME_COLUMN_NAME).setType(BYTES_TYPE_NAME),
                    new TableFieldSchema().setName(INDICATOR_CODE_COLUMN_NAME).setType(STRING_TYPE_NAME),
                    new TableFieldSchema().setName(VALUE_COLUMN_NAME).setType(FLOAT_TYPE_NAME),
                    new TableFieldSchema().setName(YEAR_COLUMN_NAME).setType(INTEGER_TYPE_NAME)
            )
    );


}
