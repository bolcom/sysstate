@echo off
IF "%1"=="" GOTO NoProfile

call mvn clean install tomcat:deploy -Dmaven.test.skip=true -P %1
GOTO End

:NoProfile
call mvn clean install tomcat:deploy -Dmaven.test.skip=true

:End
