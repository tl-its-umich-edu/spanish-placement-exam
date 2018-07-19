# Command line Spanish placement exam script to run from local bash with
# easy overrides.

#################

#set -x
set -e
set -u

echo "run SPE in bash"

########## Settings  (subject to change when needed)

SPE_VERSION=1.2

# jar version may change.
SPE_JAR=./target/spanish-placement-exams-${SPE_VERSION}.jar
# Spring boot profiles to use in run.
SPE_PROFILES=" --spring.profiles.include=DBG,FILEIO,QAINTEGRATION,LAPTOP "

#### environment variables for additional customization.
# location of file that holds the last finished_at time.
PERSIST_DIR=$(pwd)/PERSIST

SPE_ENV=" --test.skipRun=false --persist.persistPath=${PERSIST_DIR} "
# setup for the local debug mail server.
SPE_MAIL=" --email.mail.host=localhost --email.mail.defaultEncoding=UTF-8 --email.alwaysMailReport=TRUE  --email.mail.smtp.localhost=localhost --email.mail.smtp.port=1025 "
# additional debug options.
SPE_DEBUG=""
#SPE_DEBUG=" --logging.level.javax.mail=DEBUG --logging.level.com.sun.mail=DEBUG --email.mail.debug=TRUE "
SPE_ENV=" ${SPE_ENV} ${SPE_MAIL} ${SPE_DEBUG} "

# NOTE: to build and run SPE need to have the esbUtils.  Below is code to compile it.  This
# doesn't need to be done each time.
# (git clone --branch v2.0 https://github.com/tl-its-umich-edu/esbUtils \
#  && cd esbUtils \
#  && pwd \
#  && mvn clean install)


function buildJar {
    # will need to have checked out and built the esbUtils already.
    # rebuild spe
    mvn clean package -D maven.test.skip=true    
}

# Just for documentation
# function makeMailServer {
#     echo "create dummy mail server"
#     xterm -e "python -m smtpd -d -n -c DebuggingServer localhost:1025" &
# }

function runJar {
#    set -x

    # required directory
    if [[ ! -e ${PERSIST_DIR} ]] ; then
        echo "persist directory must exist: ${PERSIST_DIR}"
        exit 1;
    fi
    
    java -jar ${SPE_JAR} \
         ${SPE_ENV} \
         ${SPE_PROFILES}
}

# use if necessary.
buildJar
runJar
#end

