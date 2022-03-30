
dependencies {
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
    api(project(":plugin-parent:plugin"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}