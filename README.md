TEST UPDATED BRANCH #2
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
grades won't constantly be requested or processed.  If the date get
corrupted it can be adjusted manually.  See SPECIAL PROCESSING below
for details.

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
 

# Running SPE

SPE is configured to run on OpenShift.  Projects and running instances
are provided in the UM instance.

# Configuration / Properties

SPE takes advantage of the Spring profile capability. Profiles are property files are named 
using the convention of *application-\{name\}.properties*.
The file with the name *application.properties* will always be read.  Additional 
files names such that a *-{profile}* string is appended  to *application* in the properties file name
identify a file as an optional profile properties file.  
The profiles to be read  can be specified by adding the profile name
suffix to the run time 
argument *--spring.profiles.include={suffixes}*.  The files will be read in the 
order specified.  Specific property values may be specified in multiple files.  The last value
read will be used. For example the argument *--spring.profiles.include=DBG,OS-DEV* would
include the files *application.properties*, *application-DBG.properties* 
and *application-OS-DEV.properties*.  Any propery value set in the OS-DEV
profile file would be the value used by SPE.

Properties are split between secure and public properties.Secure files should
only contain information that isn't appropriate to put in a public GitHub
repository. Public properties are kept with the rest of the files in 
source control.   In OpenShift 
the secure properties are kept in project specific Secrets.  The secrets volume 
will need to be mounted as a seperate directory:  E.g. */opt/secrets*. 

In production there will typically be three properties files used:

 * application.properties - This includes values unlikely to change between DEV/QA/PROD instances.
 * application-OS-{instance}.properties - This includes only values specific to a 
 particular instance.  E.g. It will include the course number for the test SPE site 
 or for the real SPE site depending on the instance.
 * application-{secure-profile}.properties. - This will contain only the 
 information required for secure connections.  E.g. the urls, key, and secret (etc.)
 values to connect to the ESB. 

# Development

There are no explicit dependencies in SPE on either Docker or
OpenShift.  If you supply the data volumes and the services required
by SPE it should run fine in Docker on a laptop or from the command
line or in an IDE.

If there are problems that cause a test instance to end up in a loop
where it fails and then auto-deploys again either cancel the
deployment from the deployment configuration page or just reduce the
number of pods on that page to 0.

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
line or in Spunk, or at
https://kibana.openshift.dsc.umich.edu/

# OpenShift Considerations

## Creating an SPE instance
A new instance of code should be based on the deployment and build
configuration of the existing instances.  It is unlikely that a new
instance will be required.  Updating an existing instance is covered
below.

The disk volumes for the date persistence and for the OpenShift
secrets need to be mounted explicitly.  This can be done in deployment
configuration yaml or in the OpenShift UI.

## Updating a SPE instance

A SPE application instance might need to be updated for several reasons.

- *schedule*: The repetion frequency of the job may need to change.
  This is done by changing the value of intervalSeconds in the
  application properties file.
- *new image*: If there is a new build the image specification must be
updated. It shouldn't be "latest" for a production version.  Modifying
this requires editing the deployment configuration yaml.
- *application arguments*:  The list of application property files used by the script can
be modified by changing the container arguments in the deployment
configuration. The list will be different for different instances.
See the properties file section above for more details.

# SPECIAL TASKS

## Adjust the last retrieved date

In rare circumstances it may be necessary to reset the last queried
date used by SPE.  The file used to store the last retrieved date is a
simple text file stored in the file *persisted.txt*.  In OpenShift
this can be edited directly by adding a new app that just runs a bash
shell to the project and then attaching the data volume holding the
persisted data to that pod. The file can then be edited in vi.
 
