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

package app.simplecloud.simplecloud.plugin.proxy

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.request.group.update.CloudProxyGroupUpdateRequest
import app.simplecloud.simplecloud.api.template.group.CloudProxyGroup
import app.simplecloud.simplecloud.plugin.PluginBaseTest
import app.simplecloud.simplecloud.plugin.TestCloudPluginStarter
import app.simplecloud.simplecloud.plugin.startup.PluginCloudAPI
import org.junit.jupiter.api.BeforeEach
import java.util.*

/**
 * Date: 29.05.22
 * Time: 14:07
 * @author Frederick Baier
 *
 */
open class ProxyPluginBaseTest : PluginBaseTest() {

    protected var proxyServerRegistry = TestProxyServerRegistry()
    protected var selfOnlineCountProvider = TestSelfOnlineCountProvider()
    lateinit var pluginCloudAPI: PluginCloudAPI
        private set
    protected lateinit var proxyController: ProxyController

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.proxyServerRegistry = TestProxyServerRegistry()
        givenProxyGroup("Proxy")
        givenGroupProcesses("Proxy", 1)
        startPluginForProcess("Proxy-1")
    }

    private fun startPluginForProcess(name: String) {
        val processId = this.nodeCloudAPI.getProcessService().findByName(name).join().getUniqueId()
        startPluginForProcess(processId)
    }

    private fun startPluginForProcess(processId: UUID) {
        val pluginConfig = TestCloudPluginStarter(this.kubeAPI, processId).createProxyPlugin()
        val proxyPlugin = pluginConfig.plugin
        this.pluginCloudAPI = proxyPlugin.cloudAPI
        this.proxyController = proxyPlugin.proxyController
        this.selfOnlineCountProvider = pluginConfig.testSelfOnlineCountProvider
    }

    protected fun editDefaultProxyGroup(updateFunction: CloudProxyGroupUpdateRequest.() -> Unit = {}) {
        val processGroup = this.pluginCloudAPI.getProcessGroupService().findByName("Proxy").join()
        processGroup as CloudProxyGroup
        val updateRequest = processGroup.createUpdateRequest()
        updateFunction.invoke(updateRequest)
        updateRequest.submit().join()
    }

    protected fun getProxySelfProcess(): CloudProcess {
        return this.pluginCloudAPI.getProcessService().findByName(this.pluginCloudAPI.getLocalNetworkComponentName())
            .join()
    }


}