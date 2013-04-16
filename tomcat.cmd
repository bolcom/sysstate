call mvn install -Dmaven.test.skip=true
cd sysstate-server
call redeploy.bat
cd ..
