FROM eclipse-temurin:17

COPY bootstrap/build/libs/bootstrap-3.0.0-SNAPSHOT-all.jar /node.jar
#COPY plugin/build/libs/plugin-3.0.0-SNAPSHOT-all.jar /node-image/SimpleCloud-Plugin.jar

EXPOSE 8008

WORKDIR /node/
CMD [ "java", "-jar", "/node.jar" ]