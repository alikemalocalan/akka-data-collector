FROM openjdk:jre-alpine as stage0
WORKDIR /opt/docker
COPY target/docker/stage/opt /opt
USER root
RUN ["chmod", "-R", "u=rX,g=rX", "/opt/docker"]
RUN ["chmod", "u+x,g+x", ""]/opt/docker/bin/game-experience-collector