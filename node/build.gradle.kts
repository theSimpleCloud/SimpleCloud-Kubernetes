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
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")


    implementation(project(":rest-server"))
    implementation(project(":module-loader"))
    implementation(project(":application-loader"))
    api(project(":api"))
    implementation(project(":api-impl"))
    implementation(project(":api-internal"))
    implementation(project(":kubernetes:kubernetes-impl"))
    implementation(project(":distribution:distribution-hazelcast"))
    implementation(project(":database:database-mongo"))

    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")

    testImplementation(project(":database:database-inmemory"))
    testApi(project(":kubernetes:kubernetes-test"))

}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "app.simplecloud.simplecloud.node.startup.NodeMainKt"
    }
}