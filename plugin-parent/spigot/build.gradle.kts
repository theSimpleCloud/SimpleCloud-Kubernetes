

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    api(project(":plugin-parent:plugin"))
    implementation("app.simplecloud:distribution-hazelcast:1.0.4-SNAPSHOT")
}