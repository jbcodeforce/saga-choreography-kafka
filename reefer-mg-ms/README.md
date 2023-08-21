# Reefer management microservice


A simple reefer manager implementation based on Quarkus, reactive messaging and
support the SAGA choerography demonstration, but potentially demonstrate RESTful apis to support SAGA with stateful process server. 


## Running the application in dev mode

Start the docker compose (`docker-compose.yaml`) within this folder.


You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

Use the localhost:8081/q/swagger-ui to use the API.

Under e2e run `./createNewReefer.sh` to post a new Reefer container, and see a generated event to the reefers topic.

## Packaging 

* Package using the dockerfile jvm, and push the image to dockerhub.

```sh
./scripts/buildAll.sh
```

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

* build native

