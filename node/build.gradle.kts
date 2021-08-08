dependencies {
    implementation(kotlin("stdlib"))
    slim("com.google.code.gson:gson:2.8.7")
    slim("com.github.ajalt:clikt:2.8.0")
    slim("com.github.ajalt:clikt:2.8.0")
    slim("dev.morphia.morphia:core:1.6.1")

    implementation(project(":rest-server"))
    implementation(project(":api"))
    implementation(project(":api-impl"))
    implementation(project(":api-internal"))
    implementation(project(":task"))

}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "eu.thesimplecloud.simplecloud.node.startup.NodeMainKt"
    }
}