dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":api"))
    implementation(project(":api-impl"))
    implementation(project(":ignite"))
    api(project(":rest-server-base"))
    api(project(":permission"))

    api("com.fasterxml.jackson.core:jackson-core:2.12.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    api("org.json:json:20210307")
    implementation("com.auth0:java-jwt:3.19.0")
    implementation("com.google.guava:guava:31.1-jre")


}
