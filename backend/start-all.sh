#!/bin/bash

mvn -pl server-service spring-boot:run &
mvn -pl gateway-service spring-boot:run &
mvn -pl access-control spring-boot:run &
mvn -pl people-management spring-boot:run &
mvn -pl appointment-service spring-boot:run &
wait