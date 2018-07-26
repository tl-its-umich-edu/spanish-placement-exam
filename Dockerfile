FROM openjdk:8u131-jdk

MAINTAINER Teaching and Learning <its.tl.dev@umich.edu>

# TODO: set time zone to be MI?  Use NTP? See cpm (?)
# TODO: secure properties / secrets
# TODO: modify the entry point values for non-dev situations.

#### Setup environment
RUN apt-get update \
 && apt-get install -y maven

#### Get and build source
WORKDIR /tmp

## build the esbUtils (not directly available as a jar so build and install locally).
RUN git clone --branch v2.2 https://github.com/dlhaines/esbUtils \
 && cd esbUtils \
 && pwd \
 && mvn clean install

## build the SPE application
COPY . /tmp

# Don't run tests test on OpenShift build
RUN mvn clean package -D maven.test.skip=true

#### CLEAN UP container
RUN apt-get remove -y maven git \
    && apt-get autoremove -y

RUN rm -rf ~/.m2

############### assemble artifacts into /opt/spe #################

RUN mkdir -p /opt/spe-bin
RUN mv /tmp/target/spanish*jar /opt/spe-bin/spe.jar

####### NOTE: security files will handled as OS secrets
## install the configuration files

RUN mkdir -p /opt/spe/config
WORKDIR /tmp/config

RUN cp /tmp/config/*properties /opt/spe/config/
# don't insist that yml files exist.

RUN cp /tmp/config/*json /opt/spe/config/

# Create directory to store persisted information.
# It will be external storage.  SPE will automatically
# manage the contents.
RUN mkdir -p /opt/spe/persist

# ### set default command to be the SPE jar.
WORKDIR /opt/spe

# set entry point so can add arguments from "docker run" on command line.
# EX: If start with:
# docker run -e TZ=America/New_York spe_a --spring.profiles.include=ZOMBIE
# ZOMBIE will be ADDED to the list of spring profiles to include.
# 
ENTRYPOINT ["java", "-jar","/opt/spe-bin/spe.jar", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "--test.skipRun=false", \
            "--spring.profiles.include=OS"\
            ]

#end
