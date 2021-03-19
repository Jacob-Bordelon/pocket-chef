#!/bin/bash

docker build --no-cache --build-arg PORT=8000 -t pchef-udpserver:latest .
docker build --no-cache --build-arg PORT=8001 -t pchef-restserver1:latest .
docker build --no-cache --build-arg PORT=8002 -t pchef-restserver2:latest .
docker build --no-cache --build-arg PORT=8003 -t pchef-restserver3:latest .



docker run -e PORT=8000 --name udpserver --net host -d pchef-udpserver:latest
docker run -e PORT=8001 --name restserver1 --net host -d pchef-restserver1:latest
docker run -e PORT=8002 --name restserver2 --net host -d pchef-restserver2:latest
docker run -e PORT=8003 --name restserver3 --net host -d pchef-restserver3:latest

