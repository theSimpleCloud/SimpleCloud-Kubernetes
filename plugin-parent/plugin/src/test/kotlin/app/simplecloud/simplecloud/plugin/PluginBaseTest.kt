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

package app.simplecloud.simplecloud.plugin

import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.api.request.group.update.CloudLobbyGroupUpdateRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProcessGroupUpdateRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProxyGroupUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ServerProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudLobbyGroup
import app.simplecloud.simplecloud.api.template.group.CloudProxyGroup
import app.simplecloud.simplecloud.api.template.group.CloudServerGroup
import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import app.simplecloud.simplecloud.node.api.NodeCloudAPIImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 24.05.22
 * Time: 22:15
 * @author Frederick Baier
 *
 */
open class PluginBaseTest {

    private lateinit var nodeAPIBaseTest: NodeAPIBaseTest
    protected lateinit var nodeCloudAPI: NodeCloudAPIImpl
    protected lateinit var kubeAPI: KubeAPI
    protected lateinit var databaseFactory: InMemoryRepositorySafeDatabaseFactory

    @BeforeEach
    internal open fun setUp() {
        this.nodeAPIBaseTest = NodeAPIBaseTest()
        this.nodeAPIBaseTest.setUp()
        this.nodeCloudAPI = this.nodeAPIBaseTest.cloudAPI
        this.kubeAPI = this.nodeAPIBaseTest.kubeAPI
        this.databaseFactory = this.nodeAPIBaseTest.databaseFactory
        createDistributionService()
    }

    @AfterEach
    fun tearDown() {
        this.nodeAPIBaseTest.tearDown()
    }

    private fun createDistributionService() {
        this.kubeAPI.getNetworkService().createService(
            "distribution",
            ServiceSpec()
                .withClusterPort(1670)
                .withContainerPort(1670)
                .withLabels(Label("app", "simplecloud"))
        )
    }

    protected fun givenLobbyGroup(name: String, updateFunction: CloudLobbyGroupUpdateRequest.() -> Unit = {}) {
        val createRequest = this.nodeCloudAPI.getProcessGroupService().createCreateRequest(
            LobbyProcessTemplateConfiguration(
                name,
                1024,
                20,
                false,
                "test",
                true,
                0,
                null,
                0
            )
        )
        val processGroup = createRequest.submit().join() as CloudLobbyGroup
        val updateRequest = processGroup.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()
    }

    protected fun givenProxyGroup(name: String, updateFunction: CloudProxyGroupUpdateRequest.() -> Unit = {}) {
        val createRequest = this.nodeCloudAPI.getProcessGroupService().createCreateRequest(
            ProxyProcessTemplateConfiguration(
                name,
                1024,
                20,
                false,
                "test",
                true,
                0,
                null,
                25565
            )
        )
        val processGroup = createRequest.submit().join() as CloudProxyGroup
        val updateRequest = processGroup.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()
    }

    protected fun givenServerGroup(name: String, updateFunction: CloudProcessGroupUpdateRequest.() -> Unit = {}) {
        val createRequest = this.nodeCloudAPI.getProcessGroupService().createCreateRequest(
            ServerProcessTemplateConfiguration(
                name,
                1024,
                20,
                false,
                "test",
                true,
                0,
                null
            )
        )
        val processGroup = createRequest.submit().join() as CloudServerGroup
        val updateRequest = processGroup.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()
    }

    protected fun givenProcess(groupName: String, count: Int): List<CloudProcess> {
        val processes = ArrayList<CloudProcess>();
        val group = nodeCloudAPI.getProcessGroupService().findByName(groupName).join()
        for (i in 0 until count) {
            val process = nodeCloudAPI.getProcessService().createStartRequest(group).submit().join()
            processes.add(process)
        }
        return processes
    }

    protected fun givenOnlineProcess(groupName: String, count: Int) {
        val processes = givenProcess(groupName, count)
        processes.forEach {
            (it.createUpdateRequest() as InternalProcessUpdateRequest).setState(ProcessState.ONLINE).submit().join()
        }
    }

}