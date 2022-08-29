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

package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 29.08.22
 * Time: 09:40
 * @author Frederick Baier
 *
 */
class GetOfflinePlayerByUUIDMessageHandler(
    private val playerService: CloudPlayerService,
) : MessageHandler<UUID, OfflineCloudPlayerConfiguration> {

    override fun handleMessage(
        message: UUID,
        sender: NetworkComponent,
    ): CompletableFuture<OfflineCloudPlayerConfiguration> = CloudScope.future {
        val offlineCloudPlayer = playerService.findOfflinePlayerByUniqueId(message).await()
        return@future offlineCloudPlayer.toConfiguration()
    }
}