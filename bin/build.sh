#!/usr/bin/env bash
# Build a Docker image.  All required files must be in the current directory (with the docker file).

PROFILES=
# trace for debugging
set -x
set -e

# fail if there is a variable without a value.
set -u

echo "$0: build spanish placement exam docker image."

echo "dir: $(PWD)"

# This is OSX. May need to reset based on the build environment.
#MVN=/usr/local/bin/mvn
DOCKER=/usr/local/bin/docker

# set timezone explicitly
TIMEZONE=" -e TZ=America/New_York "

DOCKER_TAG=spe_b

echo ">>>>>>> FIX BUG <<<<<<<< Should not skip unit tests."
TEMP_ARGS=" -D maven.test.skip=true "

# build the war file
# maybe not needed (if done in docker file)
# (
# #    cd ..;
#  echo "mvn clean package ${PROFILES} ${TEMP_ARGS}"
#  ${MVN} clean package ${PROFILES} ${TEMP_ARGS}
# )

# copy configuration to build directory.
# not needed if build above docker directory
#cp -rp ../config .


# copy jar down to build directory
#cp ../target/*jar .
#cp ./target/*jar .

# Dockerfile has the mvn commands
#time ${DOCKER} build --no-cache -t ${DOCKER_TAG} .
time ${DOCKER} build -t ${DOCKER_TAG} .

#echo "current directory: " $(pwd)
# remove the temporary directory for config files
#rm -rf ./config

#end
