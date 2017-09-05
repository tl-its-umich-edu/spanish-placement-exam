#!/bin/bash

# Create a CronJob named with the current time stamp.

# TODO: make more general (multiple variables, different templates, ...)
# TODO: read variables from settings file, allow defaults and overrides.
# TODO: add secrets

# Example commands for maintenance.  Commands can accept a 
# label to limit consideration e.g. oc get jobs -l parent=speCronJob

# delete cronjob                    - oc delete cronjob/spedev
# list leftover jobs (not cron job) - oc get job
# list jobs from specific cronjob   - oc get jobs -l parent=speCronJob
# list the current extant pods      - oc get pods
# get rid of leftover jobs and related pods - oc delete jobs -l parent-speCronJob

HELP_string=$( cat <<'EOF'
$0: mangage OpenShift cron jobs.\n
To setup the Spanish Placement Exam use
  ./runAsCronJob.sh spe | oc create -f -
>> Currently only manages Spanish PLacement Exams but could easily be generalized.\n
Requires single argument specifying the prefix for the cron job template. \n
By default this will create a template for an OpenShift cron job and print it.\n
This will generate and load a OS cronjob yaml file based on a template file. \n
The template file will be specific to the job at hand.\n
Clean up old jobs needs to be done by hand using oc delete jobs command.  See below.\n
Other useful commands are: \n
- oc create -f - (pipe the output of this script into that to create the cronjob).\n
- oc delete cronjob/<cronjobname> \n
- oc get job \n
- oc get jobs -l parent=<parent name from template> \n
- oc get pods -l parent=<parent name from template> \n
- ooc delete jobs -l parent=<parent name from template> \n
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
       || [ "${NAME_PREFIX}" == "--help" ] || [ "${NAME_PREFIX}" == "help" ]; then
   help
fi

TS=$(niceTimestamp)
CRONJOB_NAME="${NAME_PREFIX}-${TS}-cronjob"

#cat ${NAME_PREFIX}-CronJob.yaml.TEMPLATE | sed "s/_CRONJOB_NAME_/${CRONJOB_NAME}/" | oc create -f -
cat ${NAME_PREFIX}-CronJob.yaml.TEMPLATE | sed "s/_CRONJOB_NAME_/${CRONJOB_NAME}/"

#end
