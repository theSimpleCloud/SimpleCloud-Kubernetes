#!/bin/sh
#
# SimpleCloud is a software for administrating a minecraft server network.
# Copyright (C) 2022 Frederick Baier & Philipp Eistrach
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

#REMOVE
kubectl delete deployment simplecloud
kubectl delete pod proxy3-1
kubectl delete pod meinegruppe2-1
kubectl delete pod staticlobby
kubectl delete pod content-server
kubectl delete pod build
kubectl delete pod ftp-server-template
kubectl delete svc ftp-service-template

echo Setting up service account
kubectl apply -f ./microk8s/k3s/serviceaccount.yml
kubectl apply -f ./microk8s/k3s/simplecloud-role.yml
kubectl apply -f ./microk8s/k3s/simplecloud-role-binding.yml

echo Building content-server
docker build . -f ./content-server/Dockerfile -t 192.168.64.9:30335/simplecloud3-content-server:latest
docker push 192.168.64.9:30335/simplecloud3-content-server:latest
kubectl apply -f ./microk8s/k3s/content-server-pod.yml

echo Building Cloud
docker build . -f Dockerfile_old -t 192.168.64.9:30335/simplecloud3:latest
docker push 192.168.64.9:30335/simplecloud3:latest
kubectl apply -f ./microk8s/k3s/simplecloud-depl.yml
