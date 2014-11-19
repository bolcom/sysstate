# VERSION:        1.0
# DOCKER-VERSION: 1.2.0
# AUTHOR:         Chris Kramer (sysstate@unionsoft.nl)

FROM tomcat:6-jre7
ENV SYSSTATE_HOME /usr/local/tomcat/.sysstate
RUN mkdir /usr/local/tomcat/.sysstate
RUN rm -rf /usr/local/tomcat/webapps/*
COPY ./sysstate-server/target/sysstate-server-0.96-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
