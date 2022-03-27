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
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import app.simplecloud.simplecloud.node.repository.mongo.player.MongoCloudPlayerRepository
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class CloudPlayerLoginMessageHandler @Inject constructor(
    private val playerFactory: CloudPlayerFactory,
    private val mongoPlayerRepository: MongoCloudPlayerRepository
) : MessageHandler<PlayerConnectionConfiguration, CloudPlayerConfiguration> {

    override fun handleMessage(
        message: PlayerConnectionConfiguration,
        sender: NetworkComponent
    ): CompletableFuture<CloudPlayerConfiguration> = CloudScope.future {
        return@future CloudPlayerLoginHandler(playerFactory, mongoPlayerRepository, message, sender.getName())
            .handleLogin()
    }




}