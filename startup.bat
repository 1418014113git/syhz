@echo off
set gport=8088
start java -jar ./syhz-spring-cloud-starter/target/syhz-spring-cloud-starter-0.0.1-SNAPSHOT.jar --server.port=%gport%