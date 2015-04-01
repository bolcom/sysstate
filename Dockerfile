# VERSION:        1.0
# DOCKER-VERSION: 1.2.0
# AUTHOR:         Chris Kramer (sysstate@unionsoft.nl)
# TO_COMPILE:     mvn clean package
# TO_BUILD:	      docker build --rm -t sysstate .
# TO_RUN:		  docker run -p 8080:8080 -v ~/sysstate/home:/usr/local/tomcat/.sysstate --link sysstate-mysql:mysql sysstate

FROM tomcat:6-jre7


ENV SYSSTATE_HOME /usr/local/tomcat/.sysstate

RUN mkdir /usr/local/tomcat/.sysstate
RUN rm -rf /usr/local/tomcat/webapps/*
COPY ./sysstate-server/target/sysstate.war /usr/local/tomcat/webapps/ROOT.war
COPY sysstate-docker.properties /usr/local/tomcat/sysstate.properties

VOLUME  /usr/local/tomcat/.sysstate