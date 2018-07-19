# Run Spanish placement exam script with a specific tag after image is built.
#################

#set -x
set -e
set -u

echo "run SPE in docker"

TAG=spe_b

ENVIRONMENT=" -e TZ=America/New_York "

######## setup local mappings for Docker container. ##############################
########
HOST_PERSIST=$(pwd)/tmp/persist
[ -e "${HOST_PERSIST}" ] || { echo "ERROR: [${HOST_PERSIST}] must exist" && exit 1; }
##########
CONTAINER_PERSIST=/opt/spe_persist
V_PERSIST=" -v ${HOST_PERSIST}:${CONTAINER_PERSIST} "
########
HOST_PUT_GRADE=$(pwd)/tmp/put_grade
[ -e "${HOST_PUT_GRADE}" ] || { echo "ERROR: [${HOST_PUT_GRADE}] must exist" && exit 1; }
CONTAINER_PUT_GRADE=/opt/spe/files
V_PUT_GRADE=" -v ${HOST_PUT_GRADE}:${CONTAINER_PUT_GRADE} "
########
HOST_SECRETS=$(pwd)/config
[ -e "${HOST_SECRETS}" ] || { echo "ERROR: [${HOST_SECRETS}] must exist" && exit 1; }
CONTAINER_SECRETS=/opt/secrets
V_SECRETS=" -v ${HOST_SECRETS}:${CONTAINER_SECRETS} "
########
V_LIST=" ${V_PERSIST} ${V_PUT_GRADE} ${V_SECRETS} "
########
#################################################################################

mkdir -p ${HOST_PERSIST}

# Arguments to pass to Spring Boot.
# Can override properties here.

## This provides the right mail host for running docker on a mac.
SP_OVERRIDE=" --email.mail.host=docker.for.mac.localhost --email.mail.smtp.port=1025 "
SP_PROFILES=" --spring.profiles.include=DBG,FILEIO,OS-dev,QAINTEGRATION "
SP_SECRETS_DIR=" --spring.config.location=file:/opt/secrets/ "
SP_ARGS=" ${SP_PROFILES} ${SP_SECRETS_DIR} ${SP_OVERRIDE} "

echo "Running docker image with tag [${TAG}] "

echo "docker run -it ${ENVIRONMENT} ${V_LIST} --rm ${TAG} ${SP_ARGS}"
docker run -it ${ENVIRONMENT} ${V_LIST} --rm ${TAG} ${SP_ARGS}

#end

