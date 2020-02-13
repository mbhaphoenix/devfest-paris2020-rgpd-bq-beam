#!/bin/bash

if [ -z "${GOOGLE_CLOUD_PROJECT}" ]; then
    echo "GOOGLE_CLOUD_PROJECT is unset";
    GOOGLE_CLOUD_PROJECT=$(gcloud config list --format 'value(core.project)' 2>/dev/null)
    echo  "GOOGLE_CLOUD_PROJECT has been set to '${GOOGLE_CLOUD_PROJECT}'"
else
    echo "GOOGLE_CLOUD_PROJECT already set to '${GOOGLE_CLOUD_PROJECT}'";
fi