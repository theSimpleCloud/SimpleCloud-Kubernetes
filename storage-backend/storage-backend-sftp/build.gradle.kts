

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":storage-backend"))
    implementation(project(":api"))
    implementation("commons-io:commons-io:2.9.0")
    implementation("com.hierynomus:sshj:0.27.0")
    implementation("com.jcraft:jsch:0.1.55")
}
