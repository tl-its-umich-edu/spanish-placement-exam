FROM openjdk:8u131-jdk
# TODO: set time zone to be MI?  Use NTP? See cpm (?)

## install the configuration files
RUN mkdir -p /opt/spe/config 
ADD config /opt/spe/config

# copy over jar file with standard name.
COPY *.jar /opt/spe/spe.jar
RUN chmod +x /opt/spe/*jar

# make the persist string location 
RUN mkdir -p /tmp/persist_string

#-Djava.security.egd=file:/dev/./urandom -Dtest.skipRun=false -Dspring.profiles.include=DBG,FILEIO,INTEGRATION

# Debugging
#RUN pwd
#RUN find /opt -ls 
#RUN java -version

### set default command to be the jar. 
WORKDIR /opt/spe
#CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/spe/spe.jar"]
#CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/spe/spe.jar","spring.profiles.include=DBG,FILEIO","-Dtest.skipRun=false"]

#--spring.profiles.active=development

#CMD ["java", "-jar","/opt/spe/spe.jar", "-Djava.security.egd=file:/dev/./urandom","--spring.profiles.include=DBG,FILEIO","-Dtest.skipRun=false"]
CMD ["java", "-jar","/opt/spe/spe.jar", "-Djava.security.egd=file:/dev/./urandom","--spring.profiles.include=DBG,FILEIO","--test.skipRun=false"]

#end
