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

package app.simplecloud.simplecloud.node.api.process

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 12.05.22
 * Time: 19:54
 * @author Frederick Baier
 *
 */
class NodeAPIProcessShutdownTest : NodeAPIProcessTest() {

    private lateinit var process: CloudProcess
    private lateinit var defaultGroup: CloudProcessGroup

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.defaultGroup = createLobbyGroup("MyLobby")
        this.process = this.processService.createStartRequest(this.defaultGroup).submit().join()
    }

    @Test
    fun dontStop_ProcessWillStayOnline() {
        executeUnregisterRunnable()
        assertProcessesCount(1)
    }

    @Test
    fun stopProcess_ProcessWillBeStopped() {
        this.processService.createShutdownRequest(this.process).submit().join()
        executeUnregisterRunnable()
        assertProcessesCount(0)
    }

    @Test
    fun create2ProcessesAndShutdown1_OneWillStayOnline() {
        this.processService.createStartRequest(this.defaultGroup).submit().join()
        this.processService.createShutdownRequest(this.process).submit().join()
        executeUnregisterRunnable()
        assertProcessesCount(1)
    }

    @Test
    fun create2ProcessesAndShutdown2_NoProcessesWillBeOnline() {
        val cloudProcess2 = this.processService.createStartRequest(this.defaultGroup).submit().join()
        this.processService.createShutdownRequest(this.process).submit().join()
        this.processService.createShutdownRequest(cloudProcess2).submit().join()
        executeUnregisterRunnable()
        assertProcessesCount(0)
    }

    @Test
    fun createProcess_StopContainer_ProcessWillBeUnregistered() {
        this.kubeAPI.getPodService().getPod(this.process.getName().lowercase()).delete()
        executeUnregisterRunnable()
        assertProcessesCount(0)
    }

}