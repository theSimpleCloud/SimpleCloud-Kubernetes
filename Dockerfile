#FROM adoptopenjdk:16.0.1_9-jdk-hotspot
FROM adoptopenjdk:11.0.11_9-jdk-hotspot


VOLUME /node/modules/
COPY node/build/libs/node-3.0.0-SNAPSHOT-all.jar /node.jar
COPY container/container-docker/build/libs/container-docker-3.0.0-SNAPSHOT-all.jar /node/modules/CONTAINER.jar
COPY storage-backend/storage-backend-sftp/build/libs/storage-backend-sftp-3.0.0-SNAPSHOT-all.jar /node/modules/STORAGE_BACKEND.jar

EXPOSE 8008

WORKDIR /node/
CMD [ "java", "-jar", "/node.jar" ]