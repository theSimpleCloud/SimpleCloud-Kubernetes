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

package app.simplecloud.simplecloud.node.util

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.internal.InternalCloudAPI
import app.simplecloud.simplecloud.api.internal.configutation.PlayerLoginConfiguration
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.DefaultPlayerProvider
import kotlinx.coroutines.runBlocking

/**
 * Date: 28.08.22
 * Time: 14:42
 * @author Frederick Baier
 *
 */
interface TestPlayerProvider {

    fun getResourceRequestHandler(): ResourceRequestHandler

    fun getCloudAPI(): InternalCloudAPI

    fun insertPlayerInDatabaseIfNotExist() {
        if (runCatching { findDefaultOfflinePlayer() }.isFailure)
            DefaultPlayerProvider.insertPlayerInDatabase(getResourceRequestHandler())
    }

    fun insertPlayerInDatabase() {
        DefaultPlayerProvider.insertPlayerInDatabase(getResourceRequestHandler())
    }

    fun insertPlayerInDatabaseWithPermission(permissionString: String) {
        DefaultPlayerProvider.insertPlayerWithPermission(getResourceRequestHandler(), permissionString)
    }

    fun executeLoginOnProxy1WithDefaultPlayer(): CloudPlayer {
        val connectionConfig = createDefaultPlayerConnectionConfiguration()
        return executePlayerLoginOnProxy1(connectionConfig)
    }

    fun executePlayerLoginOnProxy(config: PlayerConnectionConfiguration, proxyName: String): CloudPlayer = runBlocking {
        return@runBlocking getCloudAPI().getCloudPlayerService()
            .loginPlayer(PlayerLoginConfiguration(config, proxyName))
    }

    fun executePlayerLoginOnProxy1(config: PlayerConnectionConfiguration): CloudPlayer {
        return executePlayerLoginOnProxy(config, "Proxy-1")
    }

    fun executeLoginOnProxy1WithPermission(permissionString: String): CloudPlayer {
        insertPlayerInDatabaseWithPermission(permissionString)
        return executeLoginOnProxy1WithDefaultPlayer()
    }

    fun createDefaultPlayerConnectionConfiguration(): PlayerConnectionConfiguration {
        return DefaultPlayerProvider.createDefaultPlayerConnectionConfiguration()
    }

    fun executeLogoutOnDefaultPlayer(): Unit = runBlocking {
        getCloudAPI().getCloudPlayerService().logoutPlayer(DefaultPlayerProvider.DEFAULT_PLAYER_UUID)
    }

    fun findDefaultOfflinePlayer(): OfflineCloudPlayer = runBlocking {
        getCloudAPI().getCloudPlayerService().findOfflinePlayerByUniqueId(DefaultPlayerProvider.DEFAULT_PLAYER_UUID)
            .await()
    }

}