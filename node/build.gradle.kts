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
    implementation(project(":container"))
    implementation(project(":ignite"))

}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "eu.thesimplecloud.simplecloud.node.startup.NodeMainKt"
    }
}