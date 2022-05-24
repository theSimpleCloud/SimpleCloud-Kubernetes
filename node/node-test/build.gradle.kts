dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")

    api(project(":node:node-impl"))

    api(project(":kubernetes:kubernetes-test"))
    api(project(":distribution:distribution-test"))
    api(project(":rest-server:rest-server-base"))
    api(project(":database:database-inmemory"))
}