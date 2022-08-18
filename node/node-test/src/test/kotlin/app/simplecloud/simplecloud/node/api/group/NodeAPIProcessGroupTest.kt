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

package app.simplecloud.simplecloud.node.api.group

import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 11.05.22
 * Time: 18:17
 * @author Frederick Baier
 *
 */
open class NodeAPIProcessGroupTest : NodeAPIBaseTest() {

    protected lateinit var processGroupService: CloudProcessGroupService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.processGroupService = this.cloudAPI.getProcessGroupService()

    }


    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    protected fun createLobbyGroupConfiguration(name: String = "Lobby"): LobbyProcessTemplateConfiguration {
        return LobbyProcessTemplateConfiguration(
            name,
            512,
            20,
            false,
            "Test",
            false,
            0,
            null,
            0
        )
    }

}