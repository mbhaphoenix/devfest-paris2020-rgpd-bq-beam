#!/bin/bash

current_dir=$(pwd)
script_dir=$(dirname $0)

if [[ "$script_dir" = '.' ]] ; then
    script_dir="$current_dir"
fi

source $script_dir/incl.sh

bq mk --external_table_definition=$script_dir/../../src/main/resources/schema/bigtable-table-def.json ${GOOGLE_CLOUD_PROJECT}:df_secure.streaming_indicators_keysets