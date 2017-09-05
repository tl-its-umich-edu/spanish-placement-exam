#!/bin/bash

# Create a pod named with the current time stamp and tail log.

function niceTimestamp {
    echo $(date +"%F-%H-%M-%S")
}

SEC_BEFORE_LOG=30

TS=$(niceTimestamp)
POD="spe-${TS}"

cat pod-batch.yaml | sed "s/POD_NAME/${POD}/" | oc create -f -
# need to have pod created before logging
sleep ${SEC_BEFORE_LOG}
oc logs -f pod/${POD}

#end
