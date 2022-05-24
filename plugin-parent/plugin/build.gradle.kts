
dependencies {
    api(project(":api"))
    api(project(":api-impl"))
    testApi(project(":distribution:distribution-test"))
    testApi(project(":node"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}