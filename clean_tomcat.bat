@ECHO OFF

IF '%TOMCAT_HOME%'=='' SET LOCAL_TOMCAT_HOME=C:\tomcat6_0
SET LOCAL_APPLICATION_NAME=sysstate

ECHO Parameters set to:
ECHO LOCAL_TOMCAT_HOME=%LOCAL_TOMCAT_HOME%
ECHO LOCAL_APPLICATION_NAME=%LOCAL_APPLICATION_NAME%
set /p settingsCorrect=Are these settings correct? (y/n)

IF '%settingsCorrect%'=='y' GOTO doClean
IF '%settingsCorrect%'=='Y' GOTO doClean

goto end

:doClean
IF '%LOCAL_TOMCAT_HOME%'=='' GOTO help
IF '%LOCAL_APPLICATION_NAME%'=='' GOTO help

ECHO Cleaning temp dir...
del %LOCAL_TOMCAT_HOME%\temp\* /Q /S
rd %LOCAL_TOMCAT_HOME%\temp /Q /S
md %LOCAL_TOMCAT_HOME%\temp

ECHO Cleaning application dir: '%LOCAL_APPLICATION_NAME%'...
del %LOCAL_TOMCAT_HOME%\webapps\%LOCAL_APPLICATION_NAME%\* /Q /S
rd %LOCAL_TOMCAT_HOME%\webapps\%LOCAL_APPLICATION_NAME% /Q /S
del %LOCAL_TOMCAT_HOME%\webapps\%LOCAL_APPLICATION_NAME%.war /Q

del %LOCAL_TOMCAT_HOME%\conf\Catalina\localhost\%LOCAL_APPLICATION_NAME%.xml /Q /S
del %LOCAL_TOMCAT_HOME%\logs\* /Q /S
del %LOCAL_TOMCAT_HOME%\transaction_logs\* /Q /S


ECHO Cleaning work dir...
del %LOCAL_TOMCAT_HOME%\work\* /Q /S
rd %LOCAL_TOMCAT_HOME%\work /Q /S
md %LOCAL_TOMCAT_HOME%\work


GOTO end

:help
ECHO Either LOCAL_TOMCAT_HOME or LOCAL_APPLICATION_NAME are missing!
GOTO error

:error
EXIT /B 1

:end
ECHO All done!
EXIT /B 0