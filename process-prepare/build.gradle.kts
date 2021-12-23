
dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "app.simplecloud.simplecloud.prepare.MainKt"
        attributes["Premain-Class"] = "app.simplecloud.simplecloud.prepare.AgentMain"
    }
}