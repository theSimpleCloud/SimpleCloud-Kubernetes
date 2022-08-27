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

package app.simplecloud.simplecloud.plugin.util

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.permission.Permissions
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.template.ProcessTemplate

/**
 * Date: 29.03.22
 * Time: 15:50
 * @author Frederick Baier
 *
 */
class PlayerProcessTemplateJoinChecker(
    private val player: CloudPlayer,
    private val processTemplate: ProcessTemplate,
) {

    suspend fun isAllowedToJoin(): Boolean {
        if (processTemplate.isInMaintenance())
            return doesPlayerHasMaintenancePermission()
        if (hasJoinPermission(processTemplate))
            return doesPlayerHasTemplatesJoinPermission()
        return true
    }

    private suspend fun doesPlayerHasTemplatesJoinPermission(): Boolean {
        val groupPermission = this.processTemplate.getJoinPermission()!!
        return this.player.hasPermission(groupPermission).await()
    }

    private fun hasJoinPermission(processTemplate: ProcessTemplate): Boolean {
        return processTemplate.getJoinPermission() != null
    }

    private suspend fun doesPlayerHasMaintenancePermission(): Boolean {
        return this.player.hasPermission(Permissions.MAINTENANCE_JOIN).await()
    }

}