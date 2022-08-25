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

package app.simplecloud.simplecloud.node.api.player

import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import app.simplecloud.simplecloud.node.DefaultPlayerProvider
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 15.05.22
 * Time: 19:14
 * @author Frederick Baier
 *
 */
open class NodeAPIPlayerTest : NodeAPIBaseTest() {

    protected lateinit var cloudPlayerService: InternalCloudPlayerService
    protected lateinit var cloudProcessService: CloudProcessService
    protected lateinit var cloudProcessGroupService: CloudProcessGroupService


    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.cloudPlayerService = this.cloudAPI.getCloudPlayerService()
        this.cloudProcessService = this.cloudAPI.getProcessService()
        this.cloudProcessGroupService = this.cloudAPI.getProcessGroupService()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    protected fun insertPlayerWithPermissionInDatabase(permissionString: String) {
        DefaultPlayerProvider.insertPlayerWithPermission(this.databaseFactory, permissionString)
    }

    protected fun createDefaultPlayerConnectionConfiguration(): PlayerConnectionConfiguration {
        return DefaultPlayerProvider.createDefaultPlayerConnectionConfiguration()
    }

    protected fun givenProcesses(groupName: String, count: Int) {
        val processGroup = this.cloudProcessGroupService.findByName(groupName).join()
        for (i in 0 until count) {
            this.cloudProcessService.createStartRequest(processGroup).submit().join()
        }
    }

    protected fun givenLobbyGroup(
        name: String,
        maxPlayers: Int,
        maintenance: Boolean,
        joinPermission: String?,
    ): CloudProcessGroup {
        val config = createLobbyGroupConfig(name, maxPlayers, maintenance, joinPermission)
        return this.cloudProcessGroupService.createCreateRequest(config).submit().join()
    }

    private fun createLobbyGroupConfig(
        name: String,
        maxPlayers: Int,
        maintenance: Boolean,
        joinPermission: String?,
    ): LobbyProcessTemplateConfiguration {
        return LobbyProcessTemplateConfiguration(
            name,
            512,
            maxPlayers,
            maintenance,
            "Test",
            true,
            1,
            joinPermission,
            true,
            0
        )
    }

    protected fun givenProxyGroup(
        name: String,
        maxPlayers: Int,
        maintenance: Boolean,
        joinPermission: String?,
    ): CloudProcessGroup {
        val config = createProxyGroupConfig(name, maxPlayers, maintenance, joinPermission)
        return this.cloudProcessGroupService.createCreateRequest(config).submit().join()
    }

    private fun createProxyGroupConfig(
        name: String,
        maxPlayers: Int,
        maintenance: Boolean,
        joinPermission: String?,
    ): ProxyProcessTemplateConfiguration {
        return ProxyProcessTemplateConfiguration(
            name,
            512,
            maxPlayers,
            maintenance,
            "Test",
            true,
            0,
            joinPermission,
            true,
            25565
        )
    }

}