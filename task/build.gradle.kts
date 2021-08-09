
dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.tinylog:tinylog-api:2.3.2")
    implementation("org.tinylog:tinylog-impl:2.3.2")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "eu.thesimplecloud.simplecloud.task.TaskFutureTestKt"
    }
}