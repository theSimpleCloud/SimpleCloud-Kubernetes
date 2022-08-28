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

package app.simplecloud.simplecloud.plugin.server

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.plugin.PluginBaseTest
import app.simplecloud.simplecloud.plugin.TestCloudPluginStarter
import app.simplecloud.simplecloud.plugin.proxy.TestSelfOnlineCountProvider
import app.simplecloud.simplecloud.plugin.startup.PluginCloudAPI
import org.junit.jupiter.api.BeforeEach
import java.util.*

/**
 * Date: 03.06.22
 * Time: 18:52
 * @author Frederick Baier
 *
 */
open class ServerPluginBaseTest : PluginBaseTest() {

    protected var selfOnlineCountProvider = TestSelfOnlineCountProvider()
    protected lateinit var pluginCloudAPI: PluginCloudAPI

    @BeforeEach
    override fun setUp() {
        super.setUp()
        givenLobbyGroup("Lobby")
        givenGroupProcesses("Lobby", 1)
        startPluginForProcess("Lobby-1")
    }

    fun startPluginForProcess(name: String) {
        val processId = this.nodeCloudAPI.getProcessService().findByName(name).join().getUniqueId()
        startPluginForProcess(processId)
    }

    fun startPluginForProcess(processId: UUID) {
        val pluginConfig = TestCloudPluginStarter(this.kubeAPI, processId).createServerPlugin()
        val serverPlugin = pluginConfig.plugin
        this.pluginCloudAPI = serverPlugin.cloudAPI
        this.selfOnlineCountProvider = pluginConfig.testSelfOnlineCountProvider
    }

    fun getServerSelfProcess(): CloudProcess {
        return this.pluginCloudAPI.getProcessService().findByName(this.pluginCloudAPI.getLocalNetworkComponentName())
            .join()
    }

}