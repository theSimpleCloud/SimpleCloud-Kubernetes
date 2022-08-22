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

package app.simplecloud.simplecloud.node.player

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudPlayerRepository
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.database.api.DatabaseOfflineCloudPlayerRepository
import org.apache.logging.log4j.LogManager

/**
 * Date: 16.05.22
 * Time: 18:24
 * @author Frederick Baier
 *
 */
class CloudPlayerLogoutHandler(
    private val onlinePlayer: CloudPlayer,
    private val distributedRepository: DistributedCloudPlayerRepository,
    private val databaseOfflineCloudPlayerRepository: DatabaseOfflineCloudPlayerRepository
) {

    suspend fun handleLogout() {
        val offlineCloudPlayerConfiguration = this.onlinePlayer.toOfflinePlayer().toConfiguration()
        this.databaseOfflineCloudPlayerRepository.save(this.onlinePlayer.getUniqueId(), offlineCloudPlayerConfiguration)
        this.distributedRepository.remove(this.onlinePlayer.getUniqueId()).await()
        logger.info(
            "Player {} logged out",
            this.onlinePlayer.getName()
        )
    }

    companion object {
        private val logger = LogManager.getLogger(CloudPlayerLogoutHandler::class.java)
    }

}