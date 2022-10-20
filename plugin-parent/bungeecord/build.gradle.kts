
dependencies {
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bungeecord:4.1.2")
    api(project(":plugin-parent:plugin"))
    implementation("app.simplecloud:distribution-hazelcast:1.0.1-SNAPSHOT")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}