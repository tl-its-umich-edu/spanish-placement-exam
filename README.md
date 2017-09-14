# Spanish Placement Exam Script

This script is to allow student grades on Spanish placement exams in
Canvas to be automatically added to MPathways.  It runs
periodically as a batch script.  It needs little attention unless
there are external changes to authentication information or data
formats.  The script is silent except when there are grades processed.
If there is new grade information the script will email a
summary report to an mcommunity group.  The summary report is also
written to the log every time the script runs.

SPE will keep track of the last time it requested grade data so that
grades won't be requested or processed.  If the date get
corrupted it can be adjusted manually.  See SPECIAL PROCESSING below
for details.

# Running SPE

# Configuration / Properties

SPE takes advantage of the Spring profile capability. Property files are named 
using the Spring convention of *\{name\}.properties>.
The file with the name *application.properties* will always be read.  Additional 
*profile* files may be read.
A *-{profile}* suffix can be appended to *application* in the properties file name to
identify a file as an optional profile properties file.  These can
included, or not, by specifying a run time argument.
The profiles to be included  can be specified by adding the 
suffix to the run time 
argument *--spring.profiles.include={suffixes}*.  The files will be read in the 
order specified.  Specific property values may be specified in multiple files.  The last value
read will be used. For example the argument *--spring.profiles.include=DBG,OS-DEV* would
include the files *application.properties*, *application-DBG.properties* 
and *application-OS-DEV.properties*.  Any propery value set in the OS-DEV
profile file would be the value used by SPE.

Properties are split between secure and public properties.  In OpenShift 
the secure properties are kept in project specific Secrets.  Secure files should
only contain information that isn't appropriate to put in a public GitHub
repository. Public properties are kept with the rest of the files in 
source control.

In production there will typically be three properties files used:
 * application.properties - This includes values unlikely to change between DEV/QA/PROD instances.
 * application-OS-{instance}.properties - This includes only values specific to a 
 particular instance.  E.g. It will include the course number for the test SPE site 
 or for the real SPE site depending on the instance.
 * application-{secure-file-name}.properties. - This will contain only the 
 information required for secure connections.  E.g. the urls, key, and secret (etc.)
 values to connect to the ESB. 
 
The secure file(s) will be uploaded as OpenShift secrets.  The secrets volume 
will need to be mounted as a seperate directory:  E.g. */opt/secrets*. 

# Design
SPE is a Spring Boot java application. It runs in a continuous loop  but has 
built in pauses so that it will completely process input periodically. This 
built in wait should be replaced by explicit cron functionality but the 
options for that
are current quite limited in OpenShift.

The job is nearly stateless.  Grades are retrieved and updated
through an ESB API.  Non-secure configuration is bundled with the
application. Secure information is provided through OpenShift
Secrets.  Summary information is logged and, when there grade
activity, is sent to the MCommunity group *TBD*.

A very small amount of disk storage is used to record the last request
date.  This prevents continual re-processing of grades. 
See SPECIAL FEATURES for details.
 
# Developer stuff
 
There are no explicit dependencies in SPE on either Docker or
OpenShift.  If you supply the data volumes and the services required
by SPE it should run fine in Docker on a laptop or from the command
line or in an IDE.

# Input and Output
SPE gets data from the SPE application in the IBM ESB.  It also
maintains a small disk file storing the most recent time that it requested
grade information.

## Logs

Logs are available in the pods created by the cronjob.  The
names all start with 'spe' and end with a time stamp. If the pod
appears in the Applications/Pods list for the project you can get the
log from the UI. Additional logs should be available using the "View
Archive" tab on that page.  Logs may also be available via the command
line or in Spunk, or OpenShift may also be available in Splunk or at
https://kibana.openshift.dsc.umich.edu/

# OpenShift

Since cron jobs are aren't available in the OpenShift UI SPE is
administered from the command line.  CronJobs and their underlying
Jobs don't generate a deployment configuration and don't show up in
the web overview or in the list of resources in the UI.  The
pods that the jobs end up launching will be in the list of pods

 Some  administrative scripts are provided in the bin directory
in the project.

*runAsCronJob.sh* will generate a cron job configuration based on the
speCronJob.yaml.TEMPLATE. Simply running runAsCronJob.sh with no argument
will print some help on using the OpenShift CLI to administer the
application.  It also describes how to install the configuration in OpenShift.

The cron job template is not fully general.  It contains some
specific values for the current runtime environment.  In particular it
names the storage volumes used for configuration and disk files.
These will vary in the different run time instances (dev, qa,
production).  The template will need some manual adjustment for each
environment.  It will automatically label the cronjob propertly and
will provide a unique name for the job.

*listCronJobInfo.sh* lists the current existing cronjobs, jobs and
 pods for the project.  It's useful in generating the list of
 artifacts that need to be periodically cleaned up.  That cleanup is
 not automated.

*runDockerSPE.sh* is a sample script for running SPE in Docker
locally.  You will need to adjust the specifics for your needs.

*getSPE.sh* provides a sample framework for direct queries to the ESB
 for debugging purposes.

## Updating SPE

The SPE application might need to be updated for several reasons.  The
updates are very likely to be installed by updating the Cron Job
template and reinstalling it.  Items likely to require adjustment in
the cron job template are:

- schedule: The time and frequency of the job may need to change.
- image: If there is a new build the image specification must be
updated. It shouldn't be "latest" for a production version.
- args: The list of application property files used by the script can
be modified in the arguments. The non-secure application properties
are supplied in the build.  Only *application.properties* is used by
default.  Others can be added by adding them to the
*spring.profiles.include* line in the arguments section. These will
vary by the desired use of the application instance.

For each dev/qa/prod instance the volume identifiers will need to be
set. They are unlikely to change after they are first created.

### Update the Cron Job

WRONG WRONG WRONG

To update the cron job:

1. Use the *runAsCronJob.sh* script to generate the yaml needed to
install the new cronjob.
1. Adjust that yaml as required.  E.g. the cron schedule or startup
arguments may have changed.
1. Use *listCronJobInfo.sh* to list artifacts that should be cleaned
up.
1. Clean them up with *oc delete ...* The label can be very helpful in
selecting what needs to be cleaned up.
1. Upload the revised yaml with *oc create -f <filename>*

# SPECIAL PROCESSING

## Clean up artifacts
Since the CronJob object doesn't clean up it's jobs or pods they will
accumulate and need to be cleaned up periodically.  The script
*listCronJobInfo.sh* will identify what needs to be cleaned up.

An enhancement could be to have the *runAsCronJob.sh*  script run
clean up before installing a new cronjob but that would only apply
when changing a cronjob.

## Adjust the last retrieved date

In rare circumstances it may be necessary to reset the last queried
date used by SPE.  The file used to store the last retrieved date is a
simple text file stored in the file *persisted.txt*.  In OpenShift
this can be edited directly by adding a new app that just runs a bash
shell to the project and then attaching the data volume holding the
persisted data to that pod. The file can then be edited in vi.
 
## Adjusting run frequency
 
To modify the cron job run frequency adjust the speCronJob template
and delete then recreate the job.  Be aware of the current run
schedule since you need to be careful to make the change at a time
when the old job isn't running but before the time when the new job
needs to run.

