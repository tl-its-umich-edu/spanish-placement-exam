# Run Spanish placement exam script with a specific tag after image is built.
#################

set -x
echo "run SPE in docker"

TAG=spe_a

ENVIRONMENT=" -e TZ=America/New_York "

HOST_PERSIST=${PWD}/tmp/persist
CONTAINER_PERSIST=/tmp/persist_string
V_PERSIST=" -v ${HOST_PERSIST}:${CONTAINER_PERSIST} "

HOST_PUT_GRADE=${PWD}/tmp/put_grade
CONTAINER_PUT_GRADE=/opt/spe/files
V_PUT_GRADE=" -v ${HOST_PUT_GRADE}:${CONTAINER_PUT_GRADE} "

mkdir -p ${HOST_LOG}
mkdir -p ${HOST_PERSIST}

# Arguments to pass to Spring Boot.
#SP_ARGS=" --spring.profiles.include=INTEGRATION "

echo "Running docker image with tag [${TAG}] with environment [${ENVIRONMENT}] and extra args: [${SP_ARGS}]"
docker run -it ${ENVIRONMENT} ${V_PERSIST} ${V_PUT_GRADE} --rm ${TAG} ${SP_ARGS}

#end

