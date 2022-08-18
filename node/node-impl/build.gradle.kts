dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.apache.commons:commons-lang3:3.12.0")


    api(project(":api-impl"))
    implementation(project(":rest-server:rest-server-api"))
    implementation(project(":kubernetes:kubernetes-api"))
    implementation(project(":database:database-api"))

    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")

}