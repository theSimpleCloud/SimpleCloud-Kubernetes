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
    apply(java)

    dependencies {
        implementation(kotlin("stdlib"))
        testCompile("junit", "junit", "4.12")
    }
}




