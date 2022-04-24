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

rootProject.name = "simplecloud"
include("api")
include("api-impl")
include("api-internal")
include("event-api")
include("distribution")
include("application-loader")
include("module-loader")
include("kubernetes")
include("rest-server")
include("node")
include("plugin-parent")
include("content-server")
include("process-prepare")
include("rest-server-base")
include("plugin-parent:bungeecord")
findProject(":plugin-parent:bungeecord")?.name = "bungeecord"
include("plugin-parent:plugin")
findProject(":plugin-parent:plugin")?.name = "plugin"
include("plugin-parent:spigot")
findProject(":plugin-parent:spigot")?.name = "spigot"
include("distribution:distribution-api")
findProject(":distribution:distribution-api")?.name = "distribution-api"
include("distribution:distribution-hazelcast")
findProject(":distribution:distribution-hazelcast")?.name = "distribution-hazelcast"
include("database")
include("database:database-api")
findProject(":database:database-api")?.name = "database-api"
include("database:database-memory")
findProject(":database:database-memory")?.name = "untitled"
include("database:database-inmemory")
findProject(":database:database-inmemory")?.name = "database-inmemory"
include("database:database-mongo")
findProject(":database:database-mongo")?.name = "database-mongo"
