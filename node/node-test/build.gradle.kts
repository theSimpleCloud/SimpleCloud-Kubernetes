dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")

    api(project(":node:node-impl"))

    api(project(":kubernetes:kubernetes-test"))
    api("app.simplecloud:distribution-test:1.0.0-SNAPSHOT")
    api(project(":rest-server:rest-server-base"))
    api(project(":database:database-inmemory"))

    implementation("org.junit.jupiter:junit-jupiter:5.9.0")
}