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

package app.simplecloud.simplecloud.api.player

import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.request.player.CloudPlayerUpdateRequest
import app.simplecloud.simplecloud.api.request.player.PlayerConnectRequest
import net.kyori.adventure.audience.Audience
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 15:29
 * @author Frederick Baier
 */
interface CloudPlayer : OfflineCloudPlayer, Audience {

    fun getCurrentProxyName(): String

    fun getCurrentServerName(): String?

    fun getCurrentServer(): CompletableFuture<CloudProcess>

    fun getCurrentProxy(): CompletableFuture<CloudProcess>

    fun getPlayerConnection(): PlayerConnection

    fun createConnectRequest(process: CloudProcess): PlayerConnectRequest

    override fun createUpdateRequest(): CloudPlayerUpdateRequest

    override fun toConfiguration(): CloudPlayerConfiguration

}