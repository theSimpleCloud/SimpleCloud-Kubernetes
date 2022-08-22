dependencies {
    implementation(project(":api"))
    implementation(project(":rest-server:rest-server-base"))
    implementation("app.simplecloud:distribution-hazelcast:1.0.0-SNAPSHOT")
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation(project(":kubernetes:kubernetes-impl"))
    implementation(project(":database:database-mongo"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "app.simplecloud.simplecloud.bootstrap.NodeBootstrapKt"
    }
}
