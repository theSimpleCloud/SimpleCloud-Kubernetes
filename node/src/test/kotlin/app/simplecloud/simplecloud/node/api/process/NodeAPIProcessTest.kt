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

import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 11.05.22
 * Time: 18:17
 * @author Frederick Baier
 *
 */
open class NodeAPIProcessTest : NodeAPIBaseTest() {

    protected lateinit var processService: CloudProcessService
    protected lateinit var processGroupService: CloudProcessGroupService
    protected lateinit var defaultGroup: CloudProcessGroup

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.processService = this.cloudAPI.getProcessService()
        this.processGroupService = this.cloudAPI.getProcessGroupService()
        this.defaultGroup = createLobbyGroup("MyLobby")
    }


    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    protected fun createLobbyGroup(name: String = "Lobby"): CloudProcessGroup {
        val config = createLobbyGroupConfiguration(name)
        return this.processGroupService.createCreateRequest(config).submit().join()
    }

    protected fun assertProcessesCount(count: Int) {
        Assertions.assertEquals(count, this.processService.findAll().join().size)
    }

    private fun createLobbyGroupConfiguration(name: String = "Lobby"): LobbyProcessTemplateConfiguration {
        return LobbyProcessTemplateConfiguration(
            name,
            512,
            20,
            false,
            "Test",
            false,
            0,
            null,
            true,
            0
        )
    }

}