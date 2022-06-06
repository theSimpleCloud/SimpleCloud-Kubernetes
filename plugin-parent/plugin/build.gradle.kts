
dependencies {
    api(project(":api"))
    api(project(":api-impl"))
    testApi(project(":distribution:distribution-test"))
    testApi(project(":node:node-test"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}