# from zero to reactive cloud micro hero
Using java spring-boot reactive webflux r2dbc docker compose docker swarm and k8s kubernetes [![Build Status](https://travis-ci.org/daggerok/from-zero-to-reactive-cloud-micro-hero.svg?branch=master)](https://travis-ci.org/daggerok/from-zero-to-reactive-cloud-micro-hero)

[[toc]]

## what is here?
* ~~Configured Travis CI pipelines~~
* ~~Initialized draft and implement of VuePress documentation~~
* ~~Implemented step1 with sets of spring boot 2.2.o.RELEASE reactive applications~~
* ~~Implemented step2 with replacing in-memory map DBs -> r2dbc-postgres spring-data integration (run pg in docker)~~
* ~~Implemented step3 and Dockerize all applications using fabric8.io maven plugin~~
* ~~Implemented step4 and Dockerize multi-module application using fabric8.io maven plugin (all in one)~~
* ~~Implemented step5 with docker-compose maven plugin~~
* ~~Implemented step6 and Dockerize all applications using jib maven plugin from Google~~
* ~~Implemented step7 with docker-swarm~~

## step 1
Simple sets of applications implementation for local run

```bash
./mvnw -DskipTests

java -jar step1-speakers-rest-api-service/target/*.jar
java -jar step1-sessions-rsocket-service/target/*.jar
java -jar step1-frontend/target/*.jar

#http :8082/speakers name=max
#http :8082/speakers name=bax
#http :8082/speakers
http :8080
```

## step 2
Simple sets of r2dbc applications with postgres (in docker)

```bash
./mvnw -pl :step2-docker docker:start
#docker stop pg || true ; docker run --rm --name pg -p 5432:5432 postgres:alpine

./mvnw

java -jar step2-speakers-rest-api-service/target/*.jar
java -jar step2-sessions-rsocket-service/target/*.jar
java -jar step2-frontend/target/*.jar

#http :8085/speakers name=max
#http :8085/speakers

#http :8084/sessions name=maximum speakers=max
#http :8084/sessions

http :8083

./mvnw -pl :step2-docker docker:stop
#docker rm -f -v pg
```

## step 3
An applications in docker using fabric8.io maven plugin

```bash
./mvnw -pl :step3-postgres-fabric8 docker:build docker:start
./mvnw -pl :step3-sessions-rsocket-service-fabric8,:step3-speakers-rest-api-service-fabric8,:step3-frontend-fabric8 clean package docker:build docker:start

#http :8085/speakers name=max
#http :8084/sessions name=maximum speakers=max
http :8083

./mvnw -pl :step3-speakers-rest-api-service-fabric8,:step3-sessions-rsocket-service-fabric8,:step3-frontend-fabric8 docker:stop docker:remove
./mvnw -f step3-postgres-fabric8/ docker:stop docker:remove
```

## step 4
Dockerized multi-module application using fabric8.io maven plugin (all in one)

```bash
./mvnw -f step4-all-in-one-fabric8 clean package -DskipTests
./mvnw -f step4-all-in-one-fabric8 -pl :step4-all-in-one-fabric8 docker:build docker:start

#http :8090/sessions name=maximum speakers=max
#http :8091/speakers name=max
http :8089

./mvnw -f step4-all-in-one-fabric8 -pl :step4-all-in-one-fabric8 docker:stop docker:remove
```

## step 5
Dockerized multi-module application using docker-compose maven plugin (all in one)

```bash
./mvnw -f step5-all-in-one-docker-compose -pl :step5-all-in-one-docker-compose docker:build docker:start
./mvnw -f step5-all-in-one-docker-compose
./mvnw -f step5-all-in-one-docker-compose -pl :step5-all-in-one-docker-compose docker:stop docker:remove

#./mvnw -pl :step5-all-in-one-docker-compose docker-compose:up

#http :8093/sessions name=maximum speakers=max
#http :8094/speakers name=max
http :8092

./mvnw -pl :step5-all-in-one-docker-compose docker-compose:down
```

## step 6
Dockerized multi-module application using jib maven plugin from Google and docker-compose maven plugin (all in one)

```bash
./mvnw -pl :step6-all-in-one-google-jib -P pg-start
./mvnw -f step6-all-in-one-google-jib compile jib:dockerBuild
./mvnw -pl :step6-all-in-one-google-jib -P pg-stop

./mvnw -pl :step6-all-in-one-google-jib -P compose-create
./mvnw -pl :step6-all-in-one-google-jib -P compose-up

#http :8096/sessions name=maximum speakers=max
#http :8097/speakers name=max
http :8095

./mvnw -pl :step6-all-in-one-google-jib -P compose-down
```

## step 7
Dockerized multi-module application using docker-swarm (all in one)

_prepare docker-swarm_

```bash
docker swarm init
docker service create --name registry --publish published=5000,target=5000 registry:2
```

_run test postgres container for success tests during build_

```bash
./mvnw -pl :step7-all-in-one-docker-swarm -P pg-start
./mvnw -f step7-all-in-one-docker-swarm compile jib:build
./mvnw -pl :step7-all-in-one-docker-swarm -P pg-stop
```

_test with docker-compose_

```bash
./mvnw -pl :step7-all-in-one-docker-swarm -P compose-create
./mvnw -pl :step7-all-in-one-docker-swarm -P compose-up

#http :8099/sessions name=maximum speakers=max
#http :8100/speakers name=max
http :8098

./mvnw -pl :step7-all-in-one-docker-swarm -P compose-down
```

_docker stack deploy_

```bash
docker stack deploy --compose-file step7-all-in-one-docker-swarm/docker-compose.yml step7

#http :8100/speakers name=max                  | jq '.'
#http :8099/sessions name=maximum speakers=max | jq '.'
http :8098

docker stack rm step7
docker service rm registry
docker swarm leave --force
```

## cleanup

```bash
#docker rm -f -v `docker ps -aq`
for i in $(docker ps -aq) ; do docker rm -f -v $i ; done

#docker rmi -f `docker images -q --filter=dangling=true`
for i in $(docker images -q --filter=dangling=true) ; do docker rmi -f $i ; done

#docker rmi -f `docker images -q -f=reference='*/*step*'`
for i in $(docker images -q -f=reference='*/*step*') ; do docker rmi -f $i ; done
```
