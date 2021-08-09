plugins {
    java
    id ("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("jvm") version "1.5.20"
    id ("io.github.slimjar") version "1.2.1"
}

allprojects {
    group = "eu.thesimplecloud"
    version = "3.0.0-SNAPSHOT"

    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.github.johnrengelman.shadow")
        plugin("io.github.slimjar")
    }

    repositories {
        mavenCentral()
        maven {
            setUrl("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/")
        }
        maven {
            setUrl("https://repo.vshnv.tech/")
        }
    }

    dependencies {
        implementation("io.github.slimjar:slimjar:1.2.4")
    }

}

subprojects {

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("com.google.inject:guice:5.0.1")
        implementation("com.google.inject.extensions:guice-assistedinject:5.0.1")
        testImplementation(platform("org.junit:junit-bom:5.7.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        implementation("com.ea.async:ea-async:1.2.4")
        slim("dev.morphia.morphia:morphia-core:2.2.1")
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

    val instrumentTask by tasks.register("instrument", JavaExec::class) {
        main = "com.ea.async.instrumentation.Main"
        classpath = project.sourceSets.main.get().compileClasspath
        args = listOf("$buildDir")
    }

    val compileJava by tasks.getting(JavaCompile::class) {
        this.doLast {
            println("Instrumenting")
            instrumentTask.exec()
        }
    }

    val compileKotlin by tasks.getting(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
        this.doLast {
            println("Instrumenting")
            instrumentTask.exec()
        }
    }

}





