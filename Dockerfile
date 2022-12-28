FROM eclipse-temurin:17 AS build
COPY . /build/
WORKDIR /build/
RUN chmod +x gradlew
RUN ./gradlew test --stacktrace && ./gradlew shadowJar --stacktrace

FROM eclipse-temurin:17

COPY --from=build /build/bootstrap/build/libs/bootstrap-3.0.0-SNAPSHOT-all.jar /node.jar

EXPOSE 8008

WORKDIR /node/
CMD [ "java", "-jar", "/node.jar" ]