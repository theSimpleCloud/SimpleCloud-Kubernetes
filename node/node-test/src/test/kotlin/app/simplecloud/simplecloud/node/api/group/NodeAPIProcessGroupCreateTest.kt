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

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.node.assertContains
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Date: 11.05.22
 * Time: 17:35
 * @author Frederick Baier
 *
 */
class NodeAPIProcessGroupCreateTest : NodeAPIProcessGroupTest() {

    @Test
    fun newCluster_hasNoGroups() {
        assertEquals(0, processGroupService.findAll().join().size)
    }

    @Test
    fun normalGroupCreate_willNotFail() = runBlocking {
        val groupConfiguration = createLobbyGroupConfiguration()
        createGroup(groupConfiguration)
        assertGroupsCount(1)
        assertClusterContainsGroup(groupConfiguration)
    }

    @Test
    fun createTwoDifferentGroups_willNotFail(): Unit = runBlocking {
        val groupConfiguration = createLobbyGroupConfiguration("Lobby")
        val groupConfiguration2 = createLobbyGroupConfiguration("Test")
        createGroup(groupConfiguration)
        createGroup(groupConfiguration2)
    }

    @Test
    fun createSameGroupTwice_willFail(): Unit = runBlocking {
        val groupConfiguration = createLobbyGroupConfiguration()
        createGroup(groupConfiguration)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createGroup(groupConfiguration)
            }
        }
    }

    @Test
    fun createGroup_withNegativeMaxMemory_willFail(): Unit = runBlocking {
        val groupConfiguration = createGroupConfigWithMemory(-5)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createGroup(groupConfiguration)
            }
        }
    }

    @Test
    fun createGroup_withTooLowMaxMemory_willFail(): Unit = runBlocking {
        val groupConfiguration = createGroupConfigWithMemory(200)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createGroup(groupConfiguration)
            }
        }
    }

    @Test
    fun createGroup_withMaxMemory(): Unit = runBlocking {
        val groupConfiguration = createGroupConfigWithMemory(512)
        runBlocking {
            createGroup(groupConfiguration)
        }
    }

    @Test
    fun createGroup_withTooLowMaxPlayers_willFail(): Unit = runBlocking {
        val groupConfiguration = createGroupConfigWithMaxPlayers(-5)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createGroup(groupConfiguration)
            }
        }
    }

    @Test
    fun createGroup_withInfiniteMaxPlayers(): Unit = runBlocking {
        val groupConfiguration = createGroupConfigWithMaxPlayers(-1)
        createGroup(groupConfiguration)
    }

    @Test
    fun createGroup_withNormalMaxPlayers(): Unit = runBlocking {
        val groupConfiguration = createGroupConfigWithMaxPlayers(22)
        createGroup(groupConfiguration)
    }

    @Test
    fun createProxyGroup_withNormalPort_willNotFail(): Unit = runBlocking {
        val groupConfiguration = createProxyGroupWithPort(25565)
        createGroup(groupConfiguration)
    }


    private fun assertClusterContainsGroup(groupConfiguration: AbstractProcessTemplateConfiguration) {
        val allGroupConfigs = processGroupService.findAll().join().map { it.toConfiguration() }
        assertContains(allGroupConfigs, groupConfiguration)
    }

    private fun assertGroupsCount(count: Int) {
        assertEquals(count, processGroupService.findAll().join().size)
    }

    private suspend fun createGroup(groupConfiguration: AbstractProcessTemplateConfiguration) {
        processGroupService.createCreateRequest(groupConfiguration).submit().await()
    }

    private fun createGroupConfigWithMaxPlayers(maxPlayers: Int): ProxyProcessTemplateConfiguration {
        return createGroupConfigWithMemoryAndMaxPlayers(512, maxPlayers)
    }

    private fun createGroupConfigWithMemory(maxMemory: Int): ProxyProcessTemplateConfiguration {
        return createGroupConfigWithMemoryAndMaxPlayers(maxMemory, 20)
    }

    private fun createGroupConfigWithMemoryAndMaxPlayers(
        maxMemory: Int,
        maxPlayers: Int,
    ): ProxyProcessTemplateConfiguration {
        return ProxyProcessTemplateConfiguration(
            "Lobby",
            maxMemory,
            maxPlayers,
            false,
            "Test",
            false,
            0,
            null,
            25565,
        )
    }

    private fun createProxyGroupWithPort(port: Int): ProxyProcessTemplateConfiguration {
        return ProxyProcessTemplateConfiguration(
            "Lobby",
            512,
            20,
            false,
            "Test",
            false,
            0,
            null,
            port,
        )
    }

}