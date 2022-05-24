dependencies {
    implementation(project(":api"))
    api(project(":node:node-impl"))
    implementation(project(":rest-server:rest-server-base"))
    implementation(project(":distribution:distribution-hazelcast"))
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation(project(":kubernetes:kubernetes-impl"))
    implementation(project(":database:database-mongo"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "app.simplecloud.simplecloud.bootstrap.NodeBootstrapKt"
    }
}
