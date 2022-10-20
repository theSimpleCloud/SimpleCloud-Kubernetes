
dependencies {
    api(project(":api"))
    api(project(":api-impl"))
    testApi("app.simplecloud:distribution-test:1.0.1-SNAPSHOT")
    testImplementation(testFixtures(project(":node")))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}