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

package app.simplecloud.simplecloud.plugin.startup.service

import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudPlayerRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudPlayerService
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 13.01.22
 * Time: 18:40
 * @author Frederick Baier
 *
 */
@Singleton
class CloudPlayerServiceImpl @Inject constructor(
    private val igniteRepository: IgniteCloudPlayerRepository,
    playerFactory: CloudPlayerFactory
) : AbstractCloudPlayerService(igniteRepository, playerFactory) {

    override fun findOfflinePlayerByName(name: String): CompletableFuture<OfflineCloudPlayer> {
        TODO("Not yet implemented")
    }

    override fun findOfflinePlayerByUniqueId(uniqueId: UUID): CompletableFuture<OfflineCloudPlayer> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOfflinePlayerInternal(configuration: OfflineCloudPlayerConfiguration) {
        TODO()
    }
}