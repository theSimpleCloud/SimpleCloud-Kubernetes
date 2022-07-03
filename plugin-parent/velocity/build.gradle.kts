
dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    kapt("com.velocitypowered:velocity-api:3.0.1")
    api(project(":plugin-parent:plugin"))
    implementation(project(":distribution:distribution-hazelcast"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}