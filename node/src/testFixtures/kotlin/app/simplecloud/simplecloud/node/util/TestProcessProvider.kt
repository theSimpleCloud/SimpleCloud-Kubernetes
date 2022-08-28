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

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.request.group.update.CloudLobbyGroupUpdateRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProcessGroupUpdateRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProxyGroupUpdateRequest
import app.simplecloud.simplecloud.api.request.statictemplate.update.StaticLobbyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.request.statictemplate.update.StaticProxyTemplateUpdateRequest
import app.simplecloud.simplecloud.api.request.statictemplate.update.StaticServerTemplateUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ServerProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudLobbyGroup
import app.simplecloud.simplecloud.api.template.group.CloudProxyGroup
import app.simplecloud.simplecloud.api.template.group.CloudServerGroup
import app.simplecloud.simplecloud.api.template.static.StaticLobbyTemplate
import app.simplecloud.simplecloud.api.template.static.StaticProxyTemplate
import app.simplecloud.simplecloud.api.template.static.StaticServerTemplate
import app.simplecloud.simplecloud.node.task.NodeStaticOnlineProcessesHandler
import kotlinx.coroutines.runBlocking

/**
 * Date: 28.08.22
 * Time: 09:48
 * @author Frederick Baier
 *
 */
interface TestProcessProvider {

    fun getCloudAPI(): CloudAPI

    private fun createDefaultLobbyTemplateConfig(name: String): LobbyProcessTemplateConfiguration {
        return LobbyProcessTemplateConfiguration(
            name,
            1024,
            20,
            false,
            "test",
            true,
            0,
            null,
            true,
            0
        )
    }

    private fun createDefaultProxyTemplateConfig(name: String): ProxyProcessTemplateConfiguration {
        return ProxyProcessTemplateConfiguration(
            name,
            1024,
            20,
            false,
            "test",
            true,
            0,
            null,
            true,
            25565
        )
    }

    private fun createDefaultServerTemplateConfig(name: String): ServerProcessTemplateConfiguration {
        return ServerProcessTemplateConfiguration(
            name,
            1024,
            20,
            false,
            "test",
            true,
            0,
            null,
            true
        )
    }


    fun givenLobbyGroup(name: String, updateFunction: CloudLobbyGroupUpdateRequest.() -> Unit = {}) {
        val createRequest = getCloudAPI().getProcessGroupService().createCreateRequest(
            createDefaultLobbyTemplateConfig(name)
        )
        val processGroup = createRequest.submit().join() as CloudLobbyGroup
        val updateRequest = processGroup.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()
    }

    fun givenProxyGroup(name: String, updateFunction: CloudProxyGroupUpdateRequest.() -> Unit = {}) {
        val createRequest = getCloudAPI().getProcessGroupService().createCreateRequest(
            createDefaultProxyTemplateConfig(name)
        )
        val processGroup = createRequest.submit().join() as CloudProxyGroup
        val updateRequest = processGroup.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()
    }

    fun givenServerGroup(name: String, updateFunction: CloudProcessGroupUpdateRequest.() -> Unit = {}) {
        val createRequest = getCloudAPI().getProcessGroupService().createCreateRequest(
            createDefaultServerTemplateConfig(name)
        )
        val processGroup = createRequest.submit().join() as CloudServerGroup
        val updateRequest = processGroup.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()
    }

    fun givenStaticLobbyTemplate(
        name: String,
        updateFunction: StaticLobbyTemplateUpdateRequest.() -> Unit = {},
    ) {
        val createRequest = getCloudAPI().getStaticProcessTemplateService().createCreateRequest(
            createDefaultLobbyTemplateConfig(name)
        )
        val staticTemplate = createRequest.submit().join() as StaticLobbyTemplate
        val updateRequest = staticTemplate.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()

        startAndStopStaticProcessesAsNeeded()
    }

    fun givenStaticProxyTemplate(
        name: String,
        updateFunction: StaticProxyTemplateUpdateRequest.() -> Unit = {},
    ) {
        val createRequest = getCloudAPI().getStaticProcessTemplateService().createCreateRequest(
            createDefaultProxyTemplateConfig(name)
        )
        val staticTemplate = createRequest.submit().join() as StaticProxyTemplate
        val updateRequest = staticTemplate.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()

        startAndStopStaticProcessesAsNeeded()
    }

    fun givenStaticServerTemplate(
        name: String,
        updateFunction: StaticServerTemplateUpdateRequest.() -> Unit = {},
    ) {
        val createRequest = getCloudAPI().getStaticProcessTemplateService().createCreateRequest(
            createDefaultServerTemplateConfig(name)
        )
        val staticTemplate = createRequest.submit().join() as StaticServerTemplate
        val updateRequest = staticTemplate.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()

        startAndStopStaticProcessesAsNeeded()
    }

    fun startAndStopStaticProcessesAsNeeded() = runBlocking {
        val nodeCloudAPI = getCloudAPI()
        NodeStaticOnlineProcessesHandler(
            nodeCloudAPI.getStaticProcessTemplateService(),
            nodeCloudAPI.getProcessService()
        ).handleProcesses()
    }

    fun changeStateOfStaticProcessToOnline(staticProcessName: String): Unit = runBlocking {
        val processService = getCloudAPI().getProcessService()
        val cloudProcess = processService.findByName(staticProcessName).await()
        val updateRequest = cloudProcess.createUpdateRequest()
        updateRequest as InternalProcessUpdateRequest
        updateRequest.setState(ProcessState.ONLINE)
        updateRequest.submit().await()
    }

    fun givenGroupProcesses(groupName: String, count: Int): List<CloudProcess> {
        val nodeCloudAPI = getCloudAPI()
        val processes = ArrayList<CloudProcess>()
        val group = nodeCloudAPI.getProcessGroupService().findByName(groupName).join()
        for (i in 0 until count) {
            val process = nodeCloudAPI.getProcessService().createStartRequest(group).submit().join()
            processes.add(process)
        }
        return processes
    }

    fun givenOnlineGroupProcesses(groupName: String, count: Int) {
        val processes = givenGroupProcesses(groupName, count)
        processes.forEach {
            (it.createUpdateRequest() as InternalProcessUpdateRequest).setState(ProcessState.ONLINE).submit().join()
        }
    }

}