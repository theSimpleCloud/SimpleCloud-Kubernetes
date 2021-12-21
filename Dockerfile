#FROM adoptopenjdk:16.0.1_9-jdk-hotspot
FROM adoptopenjdk:11.0.11_9-jdk-hotspot

COPY node/build/libs/node-3.0.0-SNAPSHOT-all.jar /node.jar
#COPY plugin/build/libs/plugin-3.0.0-SNAPSHOT-all.jar /node-image/SimpleCloud-Plugin.jar

EXPOSE 8008

WORKDIR /node/
CMD [ "java", "-jar", "/node.jar" ]