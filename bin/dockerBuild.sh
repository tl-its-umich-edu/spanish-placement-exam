#!/usr/bin/env bash
# Build a Docker image based on the local Dockerfile.

# Uncomment for debugging
#set -x
## fail if errors or undefined bash variable.
set -eu

DOCKER_TAG=spe_b

echo "$0: build spanish placement exam docker image. Docker tag: ${DOCKER_TAG}."

echo "Building in directory: $(PWD)"

# May need to reset based on location of local docker.
DOCKER=/usr/local/bin/docker

# set timezone explicitly
TIMEZONE=" -e TZ=America/New_York "

echo ">>>>>>> FIX BUG <<<<<<<< Should not skip unit tests."
TEMP_ARGS=" -D maven.test.skip=true "

# Dockerfile has contains the build commands.
# --no-cache may make build faster.  Dockerfile hacking
# might work also.
#time ${DOCKER} build --no-cache -t ${DOCKER_TAG} .
time ${DOCKER} build -t ${DOCKER_TAG} .

#end
