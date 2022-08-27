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

package app.simplecloud.simplecloud.plugin.proxy.request.handler

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.StaticProcessTemplateService
import app.simplecloud.simplecloud.api.template.ProcessLobbyTemplate
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.template.group.CloudLobbyGroup
import app.simplecloud.simplecloud.api.template.static.StaticLobbyTemplate
import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import app.simplecloud.simplecloud.plugin.util.PlayerProcessTemplateJoinChecker

class PlayerLobbyFinder(
    private val player: CloudPlayer,
    private val processService: CloudProcessService,
    private val groupService: CloudProcessGroupService,
    private val staticTemplateService: StaticProcessTemplateService,
    private val excludedGroups: List<String>,
    private val excludedProcesses: List<String>,
) {

    suspend fun findLobby(): String {
        val lobbyTemplates = getLobbyGroupsThePlayerIsAllowedToJoinSorted()
        for (lobbyTemplate in lobbyTemplates) {
            val processes = this.processService.findByTemplate(lobbyTemplate).await()
            val notExcludedProcesses = processes.filterNot { this.excludedProcesses.contains(it.getName()) }
            val notFullProcesses = notExcludedProcesses.filterNot { it.isFull() }
            if (notFullProcesses.isEmpty()) continue
            return notFullProcesses.first().getName()
        }
        throw ProxyController.NoLobbyServerFoundException()
    }

    private suspend fun getLobbyGroupsThePlayerIsAllowedToJoinSorted(): List<ProcessLobbyTemplate> {
        val lobbyTemplates = getNotExcludedTemplates()
        val allowedTemplates = lobbyTemplates.filter { isPlayerAllowedToJoin(it) }
        return allowedTemplates.sortedByDescending { it.getLobbyPriority() }
    }

    private suspend fun getNotExcludedTemplates(): Collection<ProcessLobbyTemplate> {
        return getNotExcludedStaticTemplates().union(getNotExcludedLobbyGroups())
    }

    private suspend fun getNotExcludedStaticTemplates(): List<StaticLobbyTemplate> {
        val lobbyTemplates = staticTemplateService.findAll().await().filterIsInstance<StaticLobbyTemplate>()
        return lobbyTemplates.filterNot { this.excludedProcesses.contains(it.getName()) }
    }

    private suspend fun getNotExcludedLobbyGroups(): List<CloudLobbyGroup> {
        val allGroups = this.groupService.findAll().await().filterIsInstance<CloudLobbyGroup>()
        return allGroups.filterNot { this.excludedGroups.contains(it.getName()) }
    }

    private suspend fun isPlayerAllowedToJoin(processTemplate: ProcessTemplate): Boolean {
        return PlayerProcessTemplateJoinChecker(this.player, processTemplate).isAllowedToJoin()
    }


}
