
## Simple game experiences collector based Akka-Http&amp; PostgreSQL

### For building Docker images:

sbt docker:stage

or

sbt sbt docker:stage
sbt sbt docker:publishLocal


docker build  ./target/docker/stage -t akka-collector

docker run --entrypoint /opt/docker/bin/game-experience-collector -p 8080:8080 akka-collector:latest 
