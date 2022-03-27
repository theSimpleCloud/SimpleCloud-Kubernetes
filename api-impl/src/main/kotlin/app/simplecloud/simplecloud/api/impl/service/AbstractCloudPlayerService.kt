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

package app.simplecloud.simplecloud.api.impl.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudPlayerRepository
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 18:20
 * @author Frederick Baier
 *
 */
abstract class AbstractCloudPlayerService(
    private val igniteRepository: IgniteCloudPlayerRepository,
    private val playerFactory: CloudPlayerFactory
) : InternalCloudPlayerService {

    override fun findOnlinePlayerByUniqueId(uniqueId: UUID): CompletableFuture<CloudPlayer> {
        return this.igniteRepository.find(uniqueId).thenApply { this.playerFactory.create(it) }
    }

    override fun findOnlinePlayerByName(name: String): CompletableFuture<CloudPlayer> {
        return this.igniteRepository.findByName(name).thenApply { this.playerFactory.create(it) }
    }

    override suspend fun updateOnlinePlayerInternal(configuration: CloudPlayerConfiguration) {
        this.igniteRepository.save(configuration.uniqueId, configuration).await()
    }

}