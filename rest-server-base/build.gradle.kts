
dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":api"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    api("com.fasterxml.jackson.core:jackson-core:2.12.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    api("org.json:json:20210307")

    api("app.simplecloud:simple-rest-server:1.0.4")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}