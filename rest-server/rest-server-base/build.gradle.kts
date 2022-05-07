dependencies {
    implementation(project(":api"))
    api(project(":rest-server:rest-server-api"))

    api("com.fasterxml.jackson.core:jackson-core:2.13.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.13.2")
    api("org.json:json:20211205")

    api("app.simplecloud:simple-rest-server:1.0.4")
    implementation("com.auth0:java-jwt:3.19.1")

}