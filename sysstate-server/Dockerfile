# VERSION:        1.1.2
# DOCKER-VERSION: 1.2.0
# AUTHOR:         Chris Kramer (sysstate@unionsoft.nl)
# TO_COMPILE:     mvn clean package
# TO_BUILD:	      docker build --rm -t sysstate sysstate-server
# TO_RUN:		  docker run -p 8080:8080 -v ~/sysstate/home:/usr/local/tomcat/.sysstate --link sysstate-mysql:mysql sysstate

FROM tomcat:8-jre8


ENV SYSSTATE_HOME /usr/local/tomcat/.sysstate

ADD log4j.xml /usr/local/tomcat/.sysstate/log4j.xml
ENV JAVA_OPTS -Dlog4j.configuration=file:/usr/local/tomcat/.sysstate/log4j.xml -Xmx1024m -Xms256m

RUN rm -rf /usr/local/tomcat/webapps/*
COPY /target/sysstate.war /usr/local/tomcat/webapps/ROOT.war

VOLUME  /usr/local/tomcat/.sysstate