
dependencies {
    api(project(":api"))
    api(project(":api-internal"))
    implementation( "org.apache.ignite:ignite-core:2.11.1")
    api("com.google.inject:guice:5.0.1")
    implementation("net.kyori:adventure-api:4.9.3")
}
