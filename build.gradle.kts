/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    java
    id ("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("kapt") version "1.7.0"
    kotlin("jvm") version "1.6.21"
}

allprojects {
    group = "app.simplecloud"
    version = "3.0.0-SNAPSHOT"

    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.github.johnrengelman.shadow")
        plugin("kotlin-kapt")
    }

    repositories {
        mavenCentral()
        maven("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
        maven("https://repo.fllip.de/snapshots")
    }

}


subprojects {


    val coroutinesVersion by extra("1.6.0")
    val jacksonVersion by extra("2.13.2")

    dependencies {
        implementation(kotlin("stdlib"))
        testImplementation(platform("org.junit:junit-bom:5.9.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
        implementation("com.google.guava:guava:31.1-jre")
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





