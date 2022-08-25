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

import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import app.simplecloud.simplecloud.node.task.NodeOnlineProcessHandler
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 25.08.22
 * Time: 09:53
 * @author Frederick Baier
 *
 */
class NodeAPIProcessStartWithOnlineStrategyTest : NodeAPIProcessTest() {

    private lateinit var nodeOnlineProcessHandler: NodeOnlineProcessHandler
    private lateinit var defaultGroup: CloudProcessGroup

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.defaultGroup = createLobbyGroup("MyLobby")
        this.nodeOnlineProcessHandler = NodeOnlineProcessHandler(this.cloudAPI)
    }

    @Test
    fun groupWithOnlineConfig_willStartOne() = runBlocking {
        createStartOneOnlineConfigWithGroupAsTarget(defaultGroup)

        nodeOnlineProcessHandler.handleProcesses()

        assertProcessesCount(1)
    }

    @Test
    fun inactiveGroupWithOnlineConfig_noProcessWilStart() = runBlocking {
        createStartOneOnlineConfigWithGroupAsTarget(defaultGroup)
        disableGroup(defaultGroup)

        nodeOnlineProcessHandler.handleProcesses()

        assertProcessesCount(0)
    }

    private fun disableGroup(group: CloudProcessGroup) {
        group.createUpdateRequest()
            .setActive(false)
            .submit().join()
    }

    private fun createStartOneOnlineConfigWithGroupAsTarget(group: CloudProcessGroup) {
        val onlineStrategyService = this.cloudAPI.getOnlineStrategyService()
        onlineStrategyService.createCreateRequest(createOnlineCountStrategy(group.getName())).submit()
            .join()
    }


    private fun createOnlineCountStrategy(targetGroupName: String): ProcessOnlineCountStrategyConfiguration {
        return ProcessOnlineCountStrategyConfiguration(
            "default",
            "app.simplecloud.simplecloud.node.onlinestrategy.MinOnlineStrategy",
            setOf(targetGroupName),
            mapOf(
                "min" to "1"
            )
        )
    }

}