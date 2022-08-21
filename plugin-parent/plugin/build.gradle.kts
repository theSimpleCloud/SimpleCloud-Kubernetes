
dependencies {
    api(project(":api"))
    api(project(":api-impl"))
    testApi("app.simplecloud:distribution-test:1.0.0-SNAPSHOT")
    testApi(project(":node:node-test"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}