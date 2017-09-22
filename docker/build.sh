#!/usr/bin/env bash
# Build a Docker image.  All required files must be in the current directory (with the docker file).

# trace for debugging
#set -x
# fail if there is a variable without a value.
set -e

echo "$0: build spanish placement exam docker image."

# This is OSX. May need to reset based on the build environment.
MVN=/usr/local/bin/mvn
DOCKER=/usr/local/bin/docker

# set timezone explicitly
TIMEZONE=" -e TZ=America/New_York "

DOCKER_TAG=spe_a

echo ">>>>>>> FIX BUG <<<<<<<< Should not skip unit tests."
TEMP_ARGS=" -D maven.test.skip=true "

# build the war file
(cd ..;
 echo "mvn clean package ${PROFILES} ${TEMP_ARGS}"
 ${MVN} clean package ${PROFILES} ${TEMP_ARGS}
)

# copy configuration to build directory.
cp -rp ../config .

# copy jar down to build directory
cp ../target/*jar .

${DOCKER} build -t ${DOCKER_TAG} .

#echo "current directory: " $(pwd)
# remove the temporary directory for config files
rm -rf ./config

echo -e "# Run locally with: \n#docker run ${TIMEZONE} ${DOCKER_TAG}"
#end
