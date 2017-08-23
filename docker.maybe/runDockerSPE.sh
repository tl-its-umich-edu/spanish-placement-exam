# Run spanish placement exam script
#################

set -x
echo "run SPE in docker"

# test that can specify args on startup
ARGS=" --spring.profiles.include=ZOMBIE "

docker run  -e TZ=America/New_York  spe_a ${ARGS}

#end

