docker run -it --rm -p 8080:8080 -v ~/sysstate/home:/usr/local/tomcat/.sysstate --link sysstate-mysql:mysql sysstate
