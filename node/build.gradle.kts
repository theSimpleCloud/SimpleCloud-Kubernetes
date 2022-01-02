dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation("org.tinylog:tinylog-api:2.3.2")
    implementation("org.tinylog:tinylog-impl:2.3.2")
    implementation("com.github.docker-java:docker-java:3.2.11")

    implementation(project(":rest-server"))
    implementation(project(":module-loader"))
    implementation(project(":application-loader"))
    implementation(project(":api"))
    implementation(project(":api-impl"))
    implementation(project(":api-internal"))
    implementation(project(":kubernetes"))
    implementation(project(":ignite"))

    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation("org.apache.logging.log4j:log4j-api:2.17.1")

}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "eu.thesimplecloud.simplecloud.node.startup.NodeMainKt"
    }
}