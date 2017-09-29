# Spanish Placement Exam Script

This script is to allow student grades on Spanish placement exams in
Canvas to be automatically added to MPathways.  It runs
periodically as a batch script.  It needs little attention unless
there are external changes to authentication information or data
formats.  The script is silent except when there are grades processed.
If there is new grade information the script will email a 
summary report to an Mcommunity group.  The summary report is also
written to the log every time the script runs.

SPE will keep track of the last time it requested grade data so that
grades won't constantly be requested or processed.  If, for some reason,
the stored date isn't
the one needed it can be adjusted manually.  See SPECIAL PROCESSING below
for details.

# Design
SPE is a Spring Boot java application. It runs in a continuous loop  but has 
built in waits so that it will process input several times a day. This 
built in wait should be replaced by explicit cron functionality but the 
options for that are current  limited in OpenShift.

The job is nearly state-less.  
* Scores are retrieved and updated
through an ESB API.  
* Non-secure configuration is bundled with the
application in properties files.
* Secure information is provided through OpenShift
Secrets. 
* Summary information is logged and, when there grade
activity, is sent to the MCommunity group.

A very small amount of disk storage is used to record the last request
date.  This prevents continual re-processing of grades. 
See SPECIAL FEATURES for details.
 

# Running SPE

SPE is configured to run on OpenShift.  Projects and running instances
are provided in the UM OpenShift instance.

# Configuration / Properties

SPE takes advantage of the Spring *profile* application properties files 
capability. Profiles are property files are named 
using the convention of *application-{profile_name}.properties*.
The file with the name *application.properties* will always be read.  Additional 
files with names following that convention 
are an optional profile properties files.  
The profiles to be read  can be specified by adding the profile name
suffix to the run time 
argument *--spring.profiles.include={profile_name}*. Multiple profile names can
be included. The files will be read in the 
order specified.  A property value may be set in multiple files.  The last value
read will be used. For example the argument *--spring.profiles.include=DBG,OS-DEV* would
include the files *application.properties*, *application-DBG.properties* 
and *application-OS-DEV.properties*.  Any property value set in the OS-DEV
profile file would be the value used by SPE.

Properties are split between secure and public properties. Secure files should
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
OpenShift.  If you supply the arguments, data volumes, and services required
by SPE it should run fine in Docker on a laptop or from the command
line or in an IDE.

If there are problems that cause a test instance to end up in a loop
where it fails and then auto-deploys again either cancel the
deployment from the deployment configuration page or just reduce the
number of pods on that page to 0.

The code expects a mail server to be available.  A mail server is available
in the UM OpenShift environment.  When running locally a debugging mail server
can be started with the command:

<code>    python -m smtpd -d -n -c DebuggingServer localhost:1025 & </code>

Verify that the local profile for application-???.properties has the proper
mail server host and port.  

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

- *schedule*: The repetition frequency of the job may need to change.
This is done by changing the value of intervalSeconds in the
application properties file.
- *new image*: If there is a new build the image specification must be
updated. It shouldn't be "latest" for a production version.  Modifying
this requires editing the deployment configuration yaml.
- *application arguments*:  The list of application property files used by 
the script can
be modified by changing the container arguments in the deployment
configuration yaml. The list will be different for different instances.
See the properties file section above for more details.

# SPECIAL TASKS

## Adjust the last retrieved date

In rare circumstances it may be necessary to reset the last queried
date used by SPE.  The simplest approach is to create a new environment 
variable for a deployment configuration with the required time stamp. 
The variable name is: **getgrades_gradedaftertime** and value 
should be a valid time stamp value in the format **2017-04-01 18:00:00**.  The 
application should then re-deploy and use this value for the next run.  After 
the application runs it is important to delete that environment variable and 
have
the application re-deploy again.  The value of the environment variable rules, 
os if it is not deleted that same value will be used every time the application 
runs.

 
