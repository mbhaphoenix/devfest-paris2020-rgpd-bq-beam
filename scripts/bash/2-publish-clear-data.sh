#!/bin/bash

current_dir=$(pwd)
script_dir=$(dirname $0)

if [[ "$script_dir" = '.' ]] ; then
    script_dir="$current_dir"
fi

source $script_dir/incl.sh

TOPIC_NAME=aead-streaming
INPUT_FILE_PATTERN=gs://${GOOGLE_CLOUD_PROJECT}/bigquery-public-data/json/world_bank_intl_debt/international_debt.json

NOW=$(date +"%g%m%d-%H%M")

gcloud dataflow jobs run streaming-international-debt-$NOW \
--gcs-location gs://dataflow-templates/latest/GCS_Text_to_Cloud_PubSub \
--max-workers 2 \
--parameters outputTopic=projects/${GOOGLE_CLOUD_PROJECT}/topics/${TOPIC_NAME},inputFilePattern=${INPUT_FILE_PATTERN} \
--region europe-west1 \
--zone europe-west1-b \
--staging-location gs://${GOOGLE_CLOUD_PROJECT}/dataflow/staging \
--project ${GOOGLE_CLOUD_PROJECT}