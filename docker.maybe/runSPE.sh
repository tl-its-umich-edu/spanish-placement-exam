# Run spanish placement exam script
#################

set -x
echo "run SPE in docker"
#JAR=./spanish-placement-exams-0.1.0.jar
#java -jar ${JAR}

docker run  -e TZ=America/New_York  spe_a 

#docker run spe_a

#$ java -jar target/myproject-0.0.1-SNAPSHOT.jar
#It is also possible to run a packaged application with remote debugging support enabled. This allows you to attach a debugger to your packaged application:

#$ java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n \
    #      -jar target/myproject-0.0.1-SNAPSHOT.jar

#end

