# spanish placement example utility

## Summary

Script to get SPE grades (after certain date) from Unizin data warehouse and 
put in MPathways.  Use ESB calls to get the grades and write grades.  Script
will locally store the time of the last successful request for grades to make
future requests efficient.  All other information is available via the ESB or 
is static and can be stored in properties files.

Properties
- esb connection information
- query specific information
- default or override date
- logging information
- persist string class configuration 

Design
- Spring boot batch application which is run as a script on demand.
- Use ESB to connect to Data warehouse and MPathways.
- Use WAPI java wrapper to simplify connections to the ESB.
- Use a custom simple string persistence class to allow 
storing/reading/writing a single string to preserve a small amount of data
with very low overhead.
- Use email for reporting.
- Expected to run on OpenShift and use ELK for log management.


## Production

OpenShift
- needs small persistent file store
- scheduling for daily run
- can override the last grade date in properties file or command line value. 
This allows adjustments when there is an interruption in the scheduled
running.

Run as cronjob. not a deployment. See docker directory for helper script.
Note that cronjob doesn't clean up it's jobs or pods so they will
accumulate.  Might have the cronjob creator script run clean up first before starting
a new cronjob.

How see logs for cronjob pods?  I don't see it in the interface if
there isn't a deployment.

