#!/bin/bash

# Create a CronJob named with the current time stamp and then tail the log.

# Example commands for maintenance.  Commands can accept a 
# label to limit consideration e.g. oc get jobs -l parent=speCronJob

# delete cronjob                    - oc delete cronjob/spedev
# list leftover jobs (not cron job) - oc get job
# list jobs from specific cronjob   - oc get jobs
# list the current extant pods      - oc get pods

function niceTimestamp {
    echo $(date +"%F-%H-%M-%S")
}

# Give it some time to startup before expecting the
# log to exist.
SEC_BEFORE_LOG=30
SHORT_NAME=spe

TS=$(niceTimestamp)
POD="${SHORT_NAME}-${TS}"

cat speCronJob.yaml | sed "s/POD_NAME/${POD}/" | oc create -f -

# need to have pod created before logging
#sleep ${SEC_BEFORE_LOG}
#oc logs -f pod/${POD}

oc get cronjob

#end
