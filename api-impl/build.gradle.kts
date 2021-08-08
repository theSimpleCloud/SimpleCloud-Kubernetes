
dependencies {
    api(project(":api"))
    api(project(":api-internal"))
    implementation( "org.apache.ignite:ignite-core:2.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    api("com.google.inject:guice:5.0.1")
    slim("dev.morphia.morphia:core:1.6.1")
}
