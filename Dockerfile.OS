FROM openjdk:8u131-jdk

MAINTAINER Teaching and Learning <its.tl.dev@umich.edu>

# TODO: set time zone to be MI?  Use NTP? See cpm (?)
# TODO: secure properties / secrets
# TODO: modify the entry point values for non-dev situations.

#### Setup environment
RUN apt-get update \
 && apt-get install -y emacs maven


#### Get and build source
WORKDIR /tmp

## build the esbUtils (not directly available as a jar so build and install locally).
RUN git clone --branch v2.0 https://github.com/tl-its-umich-edu/esbUtils \
 && cd esbUtils \
 && pwd \
 && mvn clean install

## build the SPE application
COPY . /tmp
# Don't run tests test on OpenShift build
RUN mvn clean package -D maven.test.skip=true

#RUN find /tmp -name spanish-placement-exams-0.1.0.jar -ls
RUN find / -name a\*properties

#RUN df -h /
#RUN du -sh ~/.m2

#### CLEAN UP container
RUN apt-get remove -y maven git \
    && apt-get autoremove -y

RUN rm -rf ~/.m2
RUN df -h /

############### assemble artifacts into /opt/spe #################

RUN mkdir -p /opt/spe/bin
RUN mv /tmp/target/spanish*jar /opt/spe/bin/spe.jar

#RUN find /opt/spe -ls

####### NOTE: security files will handled as OS secrets
## install the configuration files

#RUN find /tmp -ls

RUN mkdir -p /opt/spe/config
WORKDIR /tmp
COPY config/*properties /opt/spe/config/
COPY config/*yml /opt/spe/config/
COPY config/*json /opt/spe/config/

# ### set default command to be the SPE jar.
WORKDIR /opt/spe

# set entry point so can add arguments from "docker run" on command line.
# EX: If start with:
# docker run -e TZ=America/New_York spe_a --spring.profiles.include=ZOMBIE
# ZOMBIE will be ADDED to the list of spring profiles to include.
# 
ENTRYPOINT ["java", "-jar","/opt/spe/bin/spe.jar", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "--test.skipRun=false", \
            "--spring.profiles.include=DBG,FILEIO"\
            ]

#end
