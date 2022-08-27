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
import app.simplecloud.simplecloud.api.permission.Permissions
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup

/**
 * Date: 15.05.22
 * Time: 21:35
 * @author Frederick Baier
 *
 */
class CloudPlayerLoginJoinPermissionChecker(
    private val player: CloudPlayer,
    private val proxyProcess: CloudProcess,
    private val groupService: CloudProcessGroupService
) {

    suspend fun check() {
        val proxyGroup = this.groupService.findByName(this.proxyProcess.getProcessTemplateName()).await()
        checkProxyMaintenance(proxyGroup)
        checkProxyJoinPermission(proxyGroup)
        checkProxyOnlineCount()
    }

    private suspend fun checkProxyJoinPermission(proxyGroup: CloudProcessGroup) {
        val joinPermission = proxyGroup.getJoinPermission() ?: return
        if (!this.player.hasPermission(joinPermission).await()) {
            throw NoJoinPermissionException(proxyGroup.getName())
        }
    }

    private suspend fun checkProxyMaintenance(proxyGroup: CloudProcessGroup) {
        if (proxyGroup.isInMaintenance() && !this.player.hasPermission(Permissions.MAINTENANCE_JOIN).await()) {
            throw ProxyMaintenanceException(proxyGroup.getName())
        }
    }

    private suspend fun checkProxyOnlineCount() {
        if (this.proxyProcess.isFull() && !this.player.hasPermission(Permissions.JOIN_FULL).await()) {
            throw ProxyFullException(this.proxyProcess.getName())
        }
    }

    class ProxyFullException(name: String) : Exception("Proxy $name is full")
    class ProxyMaintenanceException(name: String) : Exception("Proxy $name is in maintenance")
    class NoJoinPermissionException(name: String) : Exception("No Permission to join Proxy $name")

}