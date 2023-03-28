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

package app.simplecloud.simplecloud.node.resource.player

import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudPlayerRepository
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPrePostProcessor
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Date: 10.03.23
 * Time: 18:00
 * @author Frederick Baier
 *
 */
class V1Beta1CloudPlayerPrePostProcessor(
    private val distributedPlayerRepository: DistributedCloudPlayerRepository,
) : ResourceVersionRequestPrePostProcessor<V1Beta1CloudPlayerSpec>() {

    override fun preGetAll(group: String, version: String, kind: String): RequestPreProcessorResult<Any> {
        return RequestPreProcessorResult.unsupportedRequest()
    }

    override fun preUpdate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1CloudPlayerSpec,
    ): RequestPreProcessorResult<V1Beta1CloudPlayerSpec> = runBlocking {

        return@runBlocking RequestPreProcessorResult.continueNormally()
    }

    override fun postUpdate(group: String, version: String, kind: String, name: String, spec: V1Beta1CloudPlayerSpec) {
        val playerUniqueId = UUID.fromString(name)
        val isPlayerOnline = isPlayerOnline(playerUniqueId)
        if (isPlayerOnline) {
            val onlinePlayer = getOnlinePlayer(playerUniqueId)
            val newOnlinePlayer =
                createOnlinePlayerConfig(onlinePlayer, spec.toOfflineCloudPlayerConfig(playerUniqueId))
            distributedPlayerRepository.save(playerUniqueId, newOnlinePlayer)
        }
    }

    override fun preDelete(
        group: String,
        version: String,
        kind: String,
        name: String,
    ): RequestPreProcessorResult<Any> {
        val playerUniqueId = UUID.fromString(name)
        val isPlayerOnline = isPlayerOnline(playerUniqueId)
        checkConstraint(!isPlayerOnline, "Player must not be online!")
        return RequestPreProcessorResult.continueNormally()
    }

    private fun createOnlinePlayerConfig(
        oldOnlinePlayer: CloudPlayerConfiguration,
        newOfflinePlayer: OfflineCloudPlayerConfiguration,
    ): CloudPlayerConfiguration {
        return CloudPlayerConfiguration(
            newOfflinePlayer.name,
            newOfflinePlayer.uniqueId,
            newOfflinePlayer.firstLogin,
            newOfflinePlayer.lastLogin,
            newOfflinePlayer.onlineTime,
            newOfflinePlayer.lastPlayerConnection,
            newOfflinePlayer.displayName,
            newOfflinePlayer.webConfig,
            newOfflinePlayer.permissionPlayerConfiguration,
            oldOnlinePlayer.connectedServerName,
            oldOnlinePlayer.connectedProxyName
        )
    }

    private fun isPlayerOnline(playerUniqueId: UUID): Boolean {
        return distributedPlayerRepository.doesExist(playerUniqueId).get()
    }

    private fun getOnlinePlayer(playerUniqueId: UUID): CloudPlayerConfiguration {
        return distributedPlayerRepository.find(playerUniqueId).join()
    }

}