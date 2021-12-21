#!/bin/sh
microk8s kubectl delete pod simplecloud
docker build . -t 192.168.64.2:32000/simplecloud3:latest
docker push 192.168.64.2:32000/simplecloud3:latest
microk8s kubectl apply -f ./microk8s/simplecloud-pod.yml
