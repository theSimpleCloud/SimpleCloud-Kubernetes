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

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.static.StaticProcessTemplate
import app.simplecloud.simplecloud.node.task.NodeOnlineProcessHandler
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 25.08.22
 * Time: 11:34
 * @author Frederick Baier
 *
 */
class NodeAPIStaticActiveTest : NodeAPIProcessTest() {

    private lateinit var nodeOnlineProcessHandler: NodeOnlineProcessHandler

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.staticTemplateService = this.cloudAPI.getStaticProcessTemplateService()
        this.processService = this.cloudAPI.getProcessService()
        this.nodeOnlineProcessHandler = NodeOnlineProcessHandler(this.cloudAPI)
    }

    @Test
    fun activeTemplate_WillStartProcess(): Unit = runBlocking {
        givenActiveTemplate()

        nodeOnlineProcessHandler.handleProcesses()

        assertProcessesCount(1)
    }

    @Test
    fun inactiveTemplate_WillStartProcess(): Unit = runBlocking {
        givenInactiveTemplate()

        nodeOnlineProcessHandler.handleProcesses()

        assertProcessesCount(0)
    }

    @Test
    fun inactiveTemplate_ChangeToActive_WillStartOne(): Unit = runBlocking {
        val template = givenInactiveTemplate()

        nodeOnlineProcessHandler.handleProcesses()
        template.createUpdateRequest().setActive(true).submit().await()
        nodeOnlineProcessHandler.handleProcesses()

        assertProcessesCount(1)
    }

    @Test
    fun activeTemplate_ChangeToInactive_WillStopProcess(): Unit = runBlocking {
        val template = givenActiveTemplate()

        nodeOnlineProcessHandler.handleProcesses()
        template.createUpdateRequest().setActive(false).submit().await()
        nodeOnlineProcessHandler.handleProcesses()
        executeUnregisterRunnable()


        assertProcessesCount(0)
    }

    private suspend fun givenActiveTemplate(): StaticProcessTemplate {
        val config = createLobbyProcessConfig("Lobby", true)
        return this.staticTemplateService.createCreateRequest(config).submit().await()
    }

    private suspend fun givenInactiveTemplate(): StaticProcessTemplate {
        val config = createLobbyProcessConfig("Lobby", false)
        return this.staticTemplateService.createCreateRequest(config).submit().await()
    }

    private fun createLobbyProcessConfig(name: String, active: Boolean): LobbyProcessTemplateConfiguration {
        return LobbyProcessTemplateConfiguration(
            name,
            1024,
            20,
            false,
            "Test",
            true,
            1,
            null,
            active,
            1
        )
    }

}