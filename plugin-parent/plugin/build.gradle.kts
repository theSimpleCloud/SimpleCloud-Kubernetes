
dependencies {
    api(project(":api"))
    api(project(":api-impl"))
    api(project(":distribution"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}