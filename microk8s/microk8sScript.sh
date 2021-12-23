#!/bin/sh
microk8s kubectl delete pod simplecloud
microk8s kubectl delete pod content-server

echo Building content-server
docker build . -f ./content-server/Dockerfile -t 192.168.64.2:32000/simplecloud3-content-server:latest
docker push 192.168.64.2:32000/simplecloud3-content-server:latest
microk8s kubectl apply -f ./microk8s/content-server-pod.yml

echo Building Cloud
docker build . -t 192.168.64.2:32000/simplecloud3:latest
docker push 192.168.64.2:32000/simplecloud3:latest
microk8s kubectl apply -f ./microk8s/simplecloud-pod.yml
