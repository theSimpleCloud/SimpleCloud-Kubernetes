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

microk8s kubectl delete pod simplecloud
microk8s kubectl delete pod content-server

echo Building content-server
docker build . -f ./content-server/Dockerfile -t 192.168.64.6:32000/simplecloud3-content-server:latest
docker push 192.168.64.6:32000/simplecloud3-content-server:latest
microk8s kubectl apply -f ./microk8s/content-server-pod.yml

echo Building Cloud
docker build . -t 192.168.64.6:32000/simplecloud3:latest
docker push 192.168.64.6:32000/simplecloud3:latest
microk8s kubectl apply -f ./microk8s/simplecloud-pod.yml
