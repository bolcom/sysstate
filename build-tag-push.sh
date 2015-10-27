mvn clean package
docker-compose rm -f || true
docker tag -f sysstate_server:latest $1