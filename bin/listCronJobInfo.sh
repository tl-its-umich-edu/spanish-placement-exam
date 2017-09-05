#!/bin/bash
LABEL=" -l parent=speCronJob "
FORMAT=" --output name "
echo "cron job information for ${LABEL}"
echo "CRONJOBS"
oc get cronjob ${LABEL} ${FORMAT}
echo "JOBS"
oc get jobs ${LABEL} ${FORMAT}
echo "PODS"
oc get pods ${LABEL} ${FORMAT}
#end
