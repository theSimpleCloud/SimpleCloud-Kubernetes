

dependencies {
    compileOnly("com.github.Minestom:Minestom:4a40805ca0")
    api(project(":plugin-parent:plugin"))
    implementation(project(":distribution:distribution-hazelcast"))
}