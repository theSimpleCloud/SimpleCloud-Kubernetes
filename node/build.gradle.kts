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

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation("org.tinylog:tinylog-api:2.3.2")
    implementation("org.tinylog:tinylog-impl:2.3.2")
    implementation("com.github.docker-java:docker-java:3.2.11")

    implementation(project(":rest-server"))
    implementation(project(":module-loader"))
    implementation(project(":application-loader"))
    implementation(project(":api"))
    implementation(project(":api-impl"))
    implementation(project(":api-internal"))
    implementation(project(":kubernetes"))
    implementation(project(":ignite"))

    implementation("net.kyori:adventure-api:4.9.3")

    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation("org.apache.logging.log4j:log4j-api:2.17.1")

}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "app.simplecloud.simplecloud.node.startup.NodeMainKt"
    }
}