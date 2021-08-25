#!/bin/sh

if [ "$#" -ne 1 ]; then
    echo "Unable to parse target. Run fly targets to check for what this could be."
    exit -1
fi

fly -t "$1" sp -c ./ci/pipelines/pipeline.yml -p multiverse-core
