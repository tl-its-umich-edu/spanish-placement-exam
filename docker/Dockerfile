FROM openjdk:8u131-jdk
# TODO: set time zone to be MI?  Use NTP? See cpm (?)

## install the configuration files
RUN mkdir -p /opt/spe/config 
ADD config/*properties /opt/spe/config/
ADD config/*yml /opt/spe/config/
ADD config/*json /opt/spe/config/

RUN find /opt/spe/config -ls

# copy over jar file with standard name.
COPY *.jar /opt/spe/spe.jar
RUN chmod +x /opt/spe/*jar

RUN mkdir -p -v /var/log/spe
RUN mkdir -p -v /tmp/persist_string

#RUN find /tmp -ls
#RUN find /var/log -ls

### set default command to be the jar. 
WORKDIR /opt/spe

# set entry point so can add arguments from "docker run" on command line.
# EX: If start with:
# docker run -e TZ=America/New_York spe_a --spring.profiles.include=ZOMBIE
# ZOMBIE will be ADDED to the list of spring profiles to include.

# 
ENTRYPOINT ["java", "-jar","/opt/spe/spe.jar", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "--test.skipRun=false", \
            "--spring.profiles.include=DBG,FILEIO"\
            ]

#end
