
dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    kapt("com.velocitypowered:velocity-api:3.0.1")
    api(project(":plugin-parent:plugin"))
    implementation("app.simplecloud:distribution-hazelcast:1.0.4-SNAPSHOT")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}