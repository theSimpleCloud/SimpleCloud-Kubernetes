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

import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.plugin.startup.CloudPlugin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Date: 21.05.22
 * Time: 21:16
 * @author Frederick Baier
 *
 */
class PluginStartTest : PluginBaseTest() {


    @Test
    fun startPluginOfNotExistingProcess_willFail() {
        Assertions.assertThrows(CloudPlugin.CloudPluginStartException::class.java) {
            startPluginForProcess(UUID.randomUUID())
        }
    }

    @Test
    fun startPluginOfNotExistingProcess2_willFail() {
        givenProxyGroup("Proxy")
        Assertions.assertThrows(CloudPlugin.CloudPluginStartException::class.java) {
            startPluginForProcess(UUID.randomUUID())
        }
    }

    @Test
    fun startPluginOfExistingProcess_willNotFail() {
        givenProxyGroup("Proxy")
        givenProcess("Proxy", 1)
        startPluginForProcess("Proxy-1")
    }

    @Test
    fun startPlugin_willBeOnline() {
        givenProxyGroup("Proxy")
        givenProcess("Proxy", 1)
        startPluginForProcess("Proxy-1")
        val process = this.nodeCloudAPI.getProcessService().findByName("Proxy-1").join()
        Assertions.assertEquals(ProcessState.ONLINE, process.getState())
    }

    private fun startPluginForProcess(name: String) {
        val processId = this.nodeCloudAPI.getProcessService().findByName(name).join().getUniqueId()
        startPluginForProcess(processId)
    }

    private fun startPluginForProcess(uniqueId: UUID) {
        TestCloudPluginStarter(this.kubeAPI, uniqueId).createProxyPlugin()
    }


}