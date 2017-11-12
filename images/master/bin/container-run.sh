#!/usr/bin/env bash

if [[ "$OSTYPE" == "darwin"* ]]; then # OSX
  DOCKER=$(realpath $(which docker))
  DOCKER_SOCK=$(realpath /var/run/docker.sock)
else
  DOCKER=$(which docker)
  DOCKER_SOCK=/var/run/docker.sock
fi

docker run \
    -d \
    --name jenkins-master \
    -p 8080:8080 \
    -p 50000:50000 \
    -v ${DOCKER}:/usr/bin/docker \
    -v ${DOCKER_SOCK}:/var/run/docker.sock \
    -v /root/workspace \
    jenkins-master &&\
    docker logs -f jenkins-master;
