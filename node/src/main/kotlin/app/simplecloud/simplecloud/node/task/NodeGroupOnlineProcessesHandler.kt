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

package app.simplecloud.simplecloud.node.task

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeProcessOnlineStrategyService
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup

class NodeGroupOnlineProcessesHandler(
    private val groupService: CloudProcessGroupService,
    private val processService: CloudProcessService,
    private val nodeProcessOnlineStrategyService: NodeProcessOnlineStrategyService,
) {

    suspend fun handleProcesses() {
        val groups = getActiveGroups()
        groups.forEach {
            GroupProcessOnlineCountHandler(it, this.processService, this.nodeProcessOnlineStrategyService).handle()
        }
    }

    private suspend fun getActiveGroups(): List<CloudProcessGroup> {
        val groups = this.groupService.findAll().await()
        return groups.filter { it.isActive() }
    }

}