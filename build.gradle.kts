plugins {
    java
    id ("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("jvm") version "1.6.10"
}

allprojects {
    group = "app.simplecloud"
    version = "3.0.0-SNAPSHOT"

    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.github.johnrengelman.shadow")
    }

    repositories {
        mavenCentral()
        maven("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")
    }

}

subprojects {

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")
        implementation("com.google.inject:guice:5.0.1")
        implementation("com.google.inject.extensions:guice-assistedinject:5.0.1")
        testImplementation(platform("org.junit:junit-bom:5.7.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        implementation("dev.morphia.morphia:morphia-core:2.2.1")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
        //implementation("io.github.slimjar:slimjar:1.2.4")
    }

    tasks.test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

}





