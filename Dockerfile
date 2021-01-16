# Stage 1: Build binaries
FROM gradle:jdk14 AS build

ARG SNAPSHOT=true
ARG BASE=development

ENV snap $SNAPSHOT
ENV base $BASE

#### BUILD JAR ####
WORKDIR /app
ADD . .
RUN gradle build

# Stage 2: Final binaries and final setup
FROM openjdk:14-jdk-slim AS final

WORKDIR /app
COPY --from=build app/build/libs/digital-beam-collector-*-withDependencies.jar digital-beam-collector.jar
COPY --from=build app/run.sh run.sh

# Backup openssl.cnf and set SECLEVEL=1
RUN cp /etc/ssl/openssl.cnf /etc/ssl/openssl.cnf.save
RUN sed -i 's/DEFAULT@SECLEVEL=2/DEFAULT@SECLEVEL=1/g' /etc/ssl/openssl.cnf

#### RUN SETTINGS ####
RUN ["chmod", "+x", "/app/run.sh"]

ENTRYPOINT ["/bin/bash", "-c", "/app/run.sh"]