#!/bin/bash

current_dir=$(pwd)
script_dir=$(dirname $0)

if [[ "$script_dir" = '.' ]] ; then
    script_dir="$current_dir"
fi

source $script_dir/incl.sh

INPUT_FILE_PATTERN=gs://${GOOGLE_CLOUD_PROJECT}/bigquery-public-data/json/world_bank_intl_debt/international_debt.json


# Export bigquery-public-data:world_bank_intl_debt.international_debt to project local GCS bucket
bq --location=US extract \
--destination_format NEWLINE_DELIMITED_JSON \
'bigquery-public-data:world_bank_intl_debt.international_debt' \
gs://${GOOGLE_CLOUD_PROJECT}/bigquery-public-data/json/world_bank_intl_debt/international_debt.json

# Exporting bigquery-public-data:world_bank_intl_debt.international_debt schema file
#bq show --schema --format=prettyjson 'bigquery-public-data:world_bank_intl_debt.international_debt' > $script_dir/../../src/main/resources/schema/internationl-debt-schema.json

# Loading data from GCS to project local dataset
bq load \
    --source_format=NEWLINE_DELIMITED_JSON \
    'df.plain_international_debt' \
    ${INPUT_FILE_PATTERN} \
    $script_dir/../../src/main/resources/schema/internationl-debt-schema.json
