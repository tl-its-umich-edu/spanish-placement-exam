#!/bin/bash

# Create a CronJob named with the current time stamp.

# TODO: make more general (multiple variables, different templates, ...)
# TODO: read variables from settings file, allow defaults and overrides.

# Example commands for maintenance.  Commands can accept a 
# label to limit consideration e.g. oc get jobs -l parent=speCronJob

# delete cronjob                    - oc delete cronjob/spedev
# list leftover jobs (not cron job) - oc get job
# list jobs from specific cronjob   - oc get jobs -l parent=speCronJob
# list the current extant pods      - oc get pods
# get rid of leftover jobs and related pods - oc delete jobs -l parent-speCronJob

HELP_string=$( cat <<'EOF'
$0: mangage OpenShift cron jobs.\n
>> Currently hard coded to deal with Spanish PLacement Exams but could easily be generalized.\n
Requires single argument specifying the prefix for the cron job template. \n
By default this will create a template for an OpenShift cron job and print it.  
This will generate and load a OS cronjob yaml file based on a template file. \n
The template file will be specific to the job at hand.\n
Clean up old jobs needs to be done by hand using oc delete jobs command.  See below.\n
Other useful commands are: \n
- oc create -f - (pipe the output of this script into that to create the cronjob).
- oc delete cronjob/<cronjobname> \n
- oc get job \n
- oc get jobs -l parent=<parent name from template> \n
- oc get pods -l parent=<parent name from template> \n
- ooc delete jobs -l parent=<parent name from template> \n
EOF
              )

set -e

function niceTimestamp {
    echo $(date +"%F-%H-%M-%S")
}

function help {
    echo -e ${HELP_string}
    exit 1;
}

#set -x

NAME_PREFIX=${1:-help}

# print help if asked
#if [ "$1" == "-h" ] || [ "$1" == "-help" ] || [ "$1" == "--help" ] || [ "$1" == "help" ]; then
if [ "${NAME_PREFIX}" == "-h" ] || [ "${NAME_PREFIX}" == "-help" ] \
       || [ "${NAME_PREFIX}" == "--help" ] || [ "${NAME_PREFIX}" == "help" ]; then
   help
fi

# Identifies the cron job.
#NAME_PREFIX=spe


TS=$(niceTimestamp)
CRONJOB_NAME="${NAME_PREFIX}-${TS}-cronjob"

#cat ${NAME_PREFIX}-CronJob.yaml.TEMPLATE | sed "s/_CRONJOB_NAME_/${CRONJOB_NAME}/" | oc create -f -
cat ${NAME_PREFIX}-CronJob.yaml.TEMPLATE | sed "s/_CRONJOB_NAME_/${CRONJOB_NAME}/"

#end
