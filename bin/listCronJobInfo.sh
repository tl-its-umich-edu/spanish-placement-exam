#!/bin/bash

HELP_string=$( cat <<EOF
$0 [-h] - Mangage cron jobs for this project.\n
To list artifacts run command with no arguments.\n
To delete artifacts use command like the following with artifacts listed:\n
 oc delete <output line>\n
 E.g.\n
 oc delete cronjob/spe-2017-09-06-16-27-18-cronjob\n
 oc delete job/spe-2017-09-06-16-27-18-cronjob-1504729800\n
 oc delete pod/spe-2017-09-06-16-27-18-cronjob-1504744200-sxqh6\n
EOF
              )

#command to add secret to pod
#oc volume dc/bash --add --type=secret --secret-name=test-secret --mount-path=/opt/secrets

set -e
set -u

function niceTimestamp {
    echo $(date +"%F-%H-%M-%S")
}

function help {
    echo -e ${HELP_string}
    exit 1;
}

# Get the prefix.
NAME_PREFIX=${1:-help}

# print help if asked
# TODO: this could be simpler with regex or options processing.
if [ "${NAME_PREFIX}" == "-h" ] || [ "${NAME_PREFIX}" == "-help" ] \
       || [ "${NAME_PREFIX}" == "--help" ] ; then
    help
    exit 1;
fi

LABEL=" -l parent=speCronJob "
FORMAT=" --output name "
echo "cron job information for ${LABEL}"
echo "CRONJOBS"
oc get cronjob ${FORMAT}
echo "JOBS"
oc get jobs ${LABEL} ${FORMAT}
echo "PODS"
oc get pods ${LABEL} ${FORMAT}

# to get logs  may need --version=<n> to see old logs
#oc logs [-f] [-p] POD [-c CONTAINER] [options]
#end
