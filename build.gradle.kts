plugins {
    java
    kotlin("jvm") version "1.4.31"
}

group = "eu.thesimplecloud"
version = "3.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
}
