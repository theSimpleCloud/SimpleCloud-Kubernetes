
dependencies {
    api(project(":api"))
    api(project(":api-impl"))
    api(project(":ignite"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}