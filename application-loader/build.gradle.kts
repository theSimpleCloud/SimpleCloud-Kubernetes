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

val guiceVersion by extra("5.1.0")

dependencies {
    api("com.fasterxml.jackson.core:jackson-core:2.12.2")
    api("com.fasterxml.jackson.core:jackson-annotations:2.12.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    implementation("com.google.inject:guice:${guiceVersion}")
    implementation("com.google.inject.extensions:guice-assistedinject:${guiceVersion}")
}