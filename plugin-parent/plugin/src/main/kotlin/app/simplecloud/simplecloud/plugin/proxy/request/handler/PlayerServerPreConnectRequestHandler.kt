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
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.service.CloudPlayerService
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.StaticProcessTemplateService
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.template.ProcessTemplateType
import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import app.simplecloud.simplecloud.plugin.proxy.request.ServerPreConnectRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerPreConnectResponse
import app.simplecloud.simplecloud.plugin.util.PlayerProcessTemplateJoinChecker

/**
 * Date: 29.03.22
 * Time: 14:44
 * @author Frederick Baier
 *
 * @throws NoSuchProcessException
 */
class PlayerServerPreConnectRequestHandler(
    private val request: ServerPreConnectRequest,
    private val processService: CloudProcessService,
    private val playerService: CloudPlayerService,
    private val groupService: CloudProcessGroupService,
    private val staticTemplateService: StaticProcessTemplateService,
) {

    private val player = this.playerService.findOnlinePlayerByUniqueId(this.request.playerConnection.uniqueId).join()

    suspend fun handle(): ServerPreConnectResponse {
        if (isConnectingToFallbackServer())
            return findLobbyForPlayer()
        val processConnectingTo = getProcessConnectingTo()
        val processTemplate = getProcessTemplateByProcess(processConnectingTo)
        checkIfPlayerAllowedToJoinRequestedTemplate(processTemplate)
        checkIfRequestedServerIsJoinable(processConnectingTo)
        return ServerPreConnectResponse(request.serverNameTo)
    }

    private suspend fun getProcessTemplateByProcess(process: CloudProcess): ProcessTemplate {
        if (process.isStatic()) {
            return this.staticTemplateService.findByName(process.getProcessTemplateName()).await()
        }
        return this.groupService.findByName(process.getProcessTemplateName()).await()
    }

    private suspend fun getProcessConnectingTo(): CloudProcess {
        try {
            return this.processService.findByName(this.request.serverNameTo).await()
        } catch (e: NoSuchElementException) {
            throw ProxyController.NoSuchProcessException()
        }
    }

    private suspend fun checkIfRequestedServerIsJoinable(process: CloudProcess) {
        if (process.getProcessType() == ProcessTemplateType.PROXY)
            throw ProxyController.IllegalGroupTypeException()
        if (process.getState() != ProcessState.ONLINE)
            throw ProxyController.ProcessNotJoinableException()
        if (process.isFull())
            throw ProxyController.ProcessFullException()
    }

    private suspend fun checkIfPlayerAllowedToJoinRequestedTemplate(template: ProcessTemplate) {
        val isAllowedToJoin = PlayerProcessTemplateJoinChecker(this.player, template).isAllowedToJoin()
        if (!isAllowedToJoin)
            throw ProxyController.NoPermissionToJoinGroupException()
    }

    private suspend fun findLobbyForPlayer(): ServerPreConnectResponse {
        val lobbyProcessName = PlayerLobbyFinder(
            this.player,
            this.processService,
            this.groupService,
            this.staticTemplateService,
            emptyList(),
            emptyList()
        ).findLobby()
        return ServerPreConnectResponse(lobbyProcessName)
    }

    private fun isConnectingToFallbackServer(): Boolean {
        return this.request.serverNameTo == "fallback"
    }

}