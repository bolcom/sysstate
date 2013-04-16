call mvn install -Dmaven.test.skip=true
cd sysstate-server
call mvn jetty:run
cd ..
