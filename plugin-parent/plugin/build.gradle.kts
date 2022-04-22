
dependencies {
    api(project(":api"))
    api(project(":api-impl"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}