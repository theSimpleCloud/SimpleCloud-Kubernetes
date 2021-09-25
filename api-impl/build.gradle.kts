
dependencies {
    api(project(":api"))
    api(project(":api-internal"))
    implementation( "org.apache.ignite:ignite-core:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    api("com.google.inject:guice:5.0.1")
}
