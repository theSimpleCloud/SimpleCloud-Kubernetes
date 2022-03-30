#FROM adoptopenjdk:16.0.1_9-jdk-hotspot
FROM adoptopenjdk:11.0.11_9-jdk-hotspot

COPY content-server/build/libs/content-server-3.0.0-SNAPSHOT-all.jar /app/content-server.jar
COPY plugin-parent/build/libs/plugin-parent-3.0.0-SNAPSHOT-all.jar /app/files/SimpleCloud-Plugin.jar

EXPOSE 8008

WORKDIR /app/
CMD [ "java", "-jar", "/app/content-server.jar" ]