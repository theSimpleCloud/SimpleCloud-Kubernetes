plugins {
    java
    kotlin("jvm") version "1.4.31"
}

allprojects {
    group = "eu.thesimplecloud"
    version = "3.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("com.google.inject:guice:5.0.1")
        testImplementation("junit", "junit", "4.12")
        implementation("com.ea.async:ea-async:1.2.3")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

}




