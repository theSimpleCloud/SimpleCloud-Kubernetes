
dependencies {
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
    api(project(":plugin-parent:plugin"))
    implementation("app.simplecloud:distribution-hazelcast:1.0.4-SNAPSHOT")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}