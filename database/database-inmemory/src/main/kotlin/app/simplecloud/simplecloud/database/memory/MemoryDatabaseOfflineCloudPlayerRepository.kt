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

package app.simplecloud.simplecloud.database.memory

import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.database.api.DatabaseOfflineCloudPlayerRepository
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 23.04.22
 * Time: 18:19
 * @author Frederick Baier
 *
 */
class MemoryDatabaseOfflineCloudPlayerRepository : InMemoryRepository<UUID, OfflineCloudPlayerConfiguration>(),
    DatabaseOfflineCloudPlayerRepository {

    override fun save(identifier: UUID, value: OfflineCloudPlayerConfiguration): CompletableFuture<Unit> {
        //need to create OfflineCloudPlayerConfiguration because input could be of type CloudPlayerConfiguration.
        //Because this is an InMemoryDB the config doesn't get serialized. Therefore it would store and return
        // online players.
        val configuration = OfflineCloudPlayerConfiguration(
            value.name,
            value.uniqueId,
            value.firstLogin,
            value.lastLogin,
            value.onlineTime,
            value.displayName,
            value.lastPlayerConnection,
            value.webConfig,
            value.permissionPlayerConfiguration
        )
        return super.save(identifier, configuration)
    }

    override fun findByName(name: String): CompletableFuture<OfflineCloudPlayerConfiguration> {
        return this.executeQueryAndFindFist { _, value -> value.name == name }
    }

}