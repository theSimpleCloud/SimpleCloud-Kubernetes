dependencies {
    implementation(kotlin("stdlib"))
    api(project(":api"))
    api(project(":api-impl"))
    api(project(":ignite"))
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    implementation("net.kyori:adventure-api:4.10.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    dependencies {
        exclude { dependency ->
            val dependenciesToCompile = listOf("kotlin", "ignite", "jackson", "javax", "app.simplecloud", "net.kyori")
            dependenciesToCompile.all {
                !dependency.name.contains(it)
            }
        }
    }
}