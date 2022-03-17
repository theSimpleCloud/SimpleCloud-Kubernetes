dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":api"))
    implementation(project(":api-impl"))
    implementation(project(":ignite"))
    implementation(project(":rest-server-base"))

    api("com.fasterxml.jackson.core:jackson-core:2.12.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    api("org.json:json:20210307")

}
