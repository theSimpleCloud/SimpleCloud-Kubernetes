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

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("com.google.inject:guice:5.0.1")
        testImplementation("junit", "junit", "4.12")
    }

}
