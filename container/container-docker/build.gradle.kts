

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.docker-java:docker-java:3.2.11")
    implementation(project(":container"))
    implementation(project(":api"))
}
