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
    implementation("org.apache.commons:commons-lang3:3.12.0")


    api(project(":api-impl"))
    api(project(":module:module-api-impl"))
    implementation(project(":rest-server:rest-server-api"))
    implementation(project(":kubernetes:kubernetes-api"))
    implementation(project(":database:database-api"))
    implementation(project(":module:module-load"))

    implementation("org.apache.logging.log4j:log4j-core:2.18.0")
    implementation("org.apache.logging.log4j:log4j-api:2.18.0")

    api("eu.thesimplecloud.jsonlib:json-lib:1.0.8")
    implementation("org.yaml:snakeyaml:2.0")

    testFixturesApi(project(":module:module-api-impl"))
    testFixturesApi(project(":kubernetes:kubernetes-test"))
    testFixturesApi(project(":database:database-inmemory"))
    testFixturesApi(project(":rest-server:rest-server-base"))
    testFixturesApi("app.simplecloud:distribution-test:1.0.4-SNAPSHOT")
    testFixturesImplementation("org.apache.commons:commons-lang3:3.12.0")
}