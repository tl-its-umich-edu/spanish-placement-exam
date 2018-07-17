# Spanish Placement Exam Script (SPE)

This script is to allow student grades on Spanish placement exams in
Canvas to be automatically added to MPathways.  It runs
periodically as a batch script.  It needs little attention unless
there are external changes to authentication information or data
formats.  The script is silent except when there are grades processed.
If there is new grade information found then the script will email a 
summary report to an Mcommunity group.  The summary report is also
written to the log every time the script runs.

SPE will keep track of the last time it requested grade data so that
grades won't constantly be requested or processed.  If, for some reason,
the stored date isn't the one needed it can be adjusted manually. 
See SPECIAL PROCESSING below for details.

# Design

SPE is implemented as a Spring Boot java application. 

The processing loop is simple:
- See if there are any new SPE grades to update.  This is based on the stored
timestamp of the most recent exam found in the last run.
- If there are then get the grades, format them for updating MPathways, and
do the update.
- Send out an email summary of the updates done.
- Sit and wait for the next time to run.

All processing is done in a single instance of the script.  Multiple instances
will fail since they will contend over access to the stored date.

The job is nearly state-less.

 * Scores are retrieved and updated
through an ESB API that talks to the Unizin Data Warehouse for getting 
information and MPathways for local updates.  
 * Non-secure configuration is bundled with the
application in properties files. 
 * Secure information is provided through separate properties files.
 * Summary information is logged and, when there grade
activity, is sent to the MCommunity group.
 * A very small amount of disk storage is used to record the most recent
time that any user finished a test.

See SPECIAL FEATURES for details.
 
# Running SPE

For development SPE can run within an IDE, from the command line, in a 
local docker container, or in a development OpenShift project.  There are 
bash scripts provided in the project that illustrate how to run the code.

## Runtime Environment
### Local state
SPE maintains a small amount of state on disk in order to store the last time
that grades were read.
### ESB
There are QA and Production instances of the SpanishPlacementScores API.  See
the configuration files for the exact names.
There is no non-production Unizin Data Warehouse, so for both
production and instances ESB APIs the scores are 
read from the Production Unizin Data Warehouse. The Spanish Placement
test itself is in a specific Canvas site in our production instance of Canvas.   
To allow for testing there are two SPE test courses, one
unpublished one for 
development testing and one public one for student use.  The score
updates done by the ESB API QA instance fail since the test MPathways instance
is not properly configured.  This actually works fine since the update path
is well tested and we don't end up dealing with massive numbers of duplicates
during testing.

### QA and Production
Outside of local development the SPE script runs as an OpenShift application.  
Non-prod and prod Projects and applications have been provisioned in the 
UM OpenShift instance.  

# Configuration / Properties

Configuration is done primarily with properties files.  
All properties files without secret information are 
included directly in the build. Note that this means changes to those files
will require re-deploying the application to pick up
the new values. 
Properties can be overridden by environment variables as necessary. 

## Property files
The file with the name *application.properties* will always be read.
For additional properties SPE takes advantage of the Spring *profile* 
application properties files 
capability. Profiles are property files are named 
using the convention of *application-{profile_name}.properties*.
 
File with names following that convention 
are optional profile properties files.
The profiles to be used are specified by adding the profile name
suffix to the run time 
argument *--spring.profiles.include={profile_name}*. Multiple profile names can
be included, separated by commas. The files will be read in the 
order given.  A property value may be set in multiple files.  The last value
read will be used. For example the argument *--spring.profiles.include=DBG,
OS-DEV* would
include the files *application.properties*, *application-DBG.properties* 
and *application-OS-DEV.properties*. Any property value set in the OS-DEV
profile file would be the value used by SPE.

Properties are split between secure and public properties. Secure files should
only contain information that isn't appropriate to put in a public GitHub
repository. Public properties are kept with the rest of the files in 
source control. In OpenShift 
the secure properties are kept in project specific Secrets. The secrets volume 
will need to be mounted as a separate directory:  E.g. */opt/secrets*. 

In production there will typically be at least three properties files used.

 * application.properties - This includes values unlikely to change
 between DEV/QA/PROD instances.
 
 * application-OS-{instance}.properties - This includes only values specific to a 
 particular instance.  E.g. It will include the course number for the test SPE site 
 or for the real SPE site depending on the instance.
 
 * application-{secure-profile}.properties. - This will contain only the 
 information required for secure connections.  E.g. the urls, key, and secret (etc.)
 values to connect to the ESB. 
 
## Overriding property file values
Properties can also be specified as environment variables or as command line
arguments.  Most properties will be set in files but it convenient
to override some values at runtime. In particular this is useful to specify the
set of properties files to read. It's also useful if logging levels
need to be adjusted.  

# Development

There are no explicit dependencies in SPE on either Docker or
OpenShift.  If you supply the arguments, data volumes, and services required
by SPE it should run fine in Docker on a laptop or from the command
line or in an IDE.

The script expects a mail server to be available.  A debug mail server is 
available
in the UM OpenShift environment.  When running outside OpenShift a debug 
mail server can be started with the command:

<code> python -m smtpd -d -n -c DebuggingServer localhost:1025 & </code>

Verify that the local profile for application-???.properties has the proper
mail server host and port.

The IO implementation is configurable so it is possible to configure SPE to 
read and/or write to files instead of the ESB.
This is useful for testing since we have very limited control 
of the systems on the other side of the ESB.  See the properties files for 
examples.

# Development Scripts
The *bin* directory contains bash scripts to build and run SPE from the command 
line or using Docker.  Refer to the existing OpenShift projects for OpenShift 
configuration.  IDE configuration depends on the IDE. 

# Testing
The application profile properties files can be very helpful for debugging.  
There are several examples in the config directory.  One interesting one is the
application-FILEIO.properties file.  It provides examples of how SPE can be 
configured to read and/or write to files rather than the ESB.  Since there isn't
an available test API to write grades and we can't write them to production
this ability is critical for testing.

# OpenShift Considerations

## Properties
By convention the OpenShift environment often uses environment variables
to set properties.  If you are tracking down a setting be sure to check 
the settings in the deployment under consideration. SPE logs the properties that
it reads so it is possible to know what the final property values are.

## Logs
When run outside of OpenShift logs are available as expected in the runtime 
environment. When running in OpenShift logs will be available via 
the command line via *oc* or in Spunk, or at
https://kibana.openshift.dsc.umich.edu/ Logs are only available for 30 days.
The logging setup is subject to change so YMMV.  Don't depend on mining
data from old log files.


# SPECIAL TASKS

## Adjust the last test date

The format of the last test date timestamp is ISO8601 compatible.  It need not 
contain
a T.  The time zone of the time stamp can be specified with an offset (or a 
trailing Z for UTC).  If no time zone is provided the time stamp is assumed to 
be in UTC.
Note that the "finished_at" time stamp from Canvas is stored 
in UTC so if the time needs to be overridden the time stamp must be translated
to UTC.

In rare circumstances it may be necessary to override the last test time stamp 
used by SPE.  The simplest approach to this is to 
edit the *persisted.txt* file on the mounted persistent disk and set it to the
required time.  This can be done by running a bash application and mounting the 
persistent disk to it.


# Things To Do (TTD)
- If it is necessary to persist additional information consider storing all the
information in a hash internally and as a json string externally.  The 
conversion is easy to do using Jackson.
- If it is necessary to store a lot of additional information consider using
SqlLite as a local database.  It doesn't need a database server and can handle 
lots of data in the familiar SQL format. It won't work for multiple processes.
- If "scheduling by waiting" isn't sufficient consider using the internal Java 
scheduling or, better yet, the scheduling provided by Spring.
