dependencies {
    implementation(kotlin("stdlib"))
    slim("com.google.code.gson:gson:2.8.7")
    slim("com.github.ajalt:clikt:2.8.0")
    slim("com.github.ajalt:clikt:2.8.0")

    implementation(project(":rest-server"))
    implementation(project(":module-loader"))
    implementation(project(":application-loader"))
    implementation(project(":api"))
    implementation(project(":api-impl"))
    implementation(project(":api-internal"))
    implementation(project(":task"))
    implementation(project(":container"))

}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "eu.thesimplecloud.simplecloud.node.startup.NodeMainKt"
    }
}