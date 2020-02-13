#!/bin/bash

current_dir=$(pwd)
script_dir=$(dirname $0)

if [[ "$script_dir" = '.' ]] ; then
    script_dir="$current_dir"
fi

source $script_dir/incl.sh


SUBSCRIPTION_NAME=aead-streaming

NOW=$(date +"%g%m%d-%H%M")

mvn clean compile exec:java -Dexec.mainClass=com.mbhaphoenix.gcp.aead.job.Driver \
     -Dexec.args="\
     --project=${GOOGLE_CLOUD_PROJECT} \
     --jobName=aead-streaming \
     --subscription=${SUBSCRIPTION_NAME} \
     --instance=aead-instance \
     --keysetsTable=keysets \
     --family=cf \
     --outputTable=df.streaming_encrypted_international_debt \
     --stagingLocation=gs://${GOOGLE_CLOUD_PROJECT}/dataflow/staging/ \
     --tempLocation=gs://${GOOGLE_CLOUD_PROJECT}/dataflow/temp/ \
     --runner=DataFlowRunner \
     --streaming=true \
     --region=europe-west1 \
     --zone=europe-west1-b \
     --numWorkers=6 \
     --maxNumWorkers=8 \
     --workerMachineType=n1-standard-1 \
     --enableStreamingEngine=true \
     --usePublicIps=true
     "



