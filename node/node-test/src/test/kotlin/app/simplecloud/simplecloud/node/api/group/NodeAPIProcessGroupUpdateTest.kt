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
import app.simplecloud.simplecloud.api.impl.image.ImageImpl
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 11.05.22
 * Time: 18:16
 * @author Frederick Baier
 *
 */
class NodeAPIProcessGroupUpdateTest : NodeAPIProcessGroupTest() {


    private lateinit var existingGroup: CloudProcessGroup

    @BeforeEach
    override fun setUp() {
        super.setUp()
        existingGroup = this.processGroupService.createCreateRequest(createLobbyGroupConfiguration()).submit().join()
    }

    @Test
    fun updateImageTest() {
        val imageName = RandomStringUtils.randomAlphabetic(16)
        this.processGroupService.createUpdateRequest(existingGroup)
            .setImage(ImageImpl.fromName(imageName))
            .submit().join()
        Assertions.assertEquals(imageName, getCurrentGroupConfig().imageName)
    }

    @Test
    fun updateMaintenanceTest() {
        this.processGroupService.createUpdateRequest(existingGroup)
            .setMaintenance(true)
            .submit().join()
        Assertions.assertEquals(true, getCurrentGroupConfig().maintenance)
    }

    @Test
    fun updateStateUpdatingTest() {
        this.processGroupService.createUpdateRequest(existingGroup)
            .setStateUpdating(true)
            .submit().join()
        Assertions.assertEquals(true, getCurrentGroupConfig().stateUpdating)
    }

    @Test
    fun updateMaxMemory_negativeInput_willFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                processGroupService.createUpdateRequest(existingGroup)
                    .setMaxMemory(-7)
                    .submit().await()
            }
        }
    }

    @Test
    fun updateMemory_withTooLowInput_willFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                processGroupService.createUpdateRequest(existingGroup)
                    .setMaxMemory(200)
                    .submit().await()
            }
        }
    }

    @Test
    fun updateMemory_with256MB() {
        processGroupService.createUpdateRequest(existingGroup)
            .setMaxMemory(256)
            .submit().join()
    }

    @Test
    fun updatePermissionTest() {
        val permission = RandomStringUtils.randomAlphabetic(16)
        processGroupService.createUpdateRequest(existingGroup)
            .setJoinPermission(permission)
            .submit().join()
        Assertions.assertEquals(permission, getCurrentGroupConfig().joinPermission)
    }

    @Test
    fun updateMaxPlayers_withTooLowInput_willFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                processGroupService.createUpdateRequest(existingGroup)
                    .setMaxPlayers(-2)
                    .submit().await()
            }
        }
    }

    @Test
    fun updateMaxPlayers_withInfinitePlayers() {
        processGroupService.createUpdateRequest(existingGroup)
            .setMaxPlayers(-1)
            .submit().join()
        Assertions.assertEquals(-1, getCurrentGroupConfig().maxPlayers)
    }

    @Test
    fun updateMaxPlayersTest() {
        processGroupService.createUpdateRequest(existingGroup)
            .setMaxPlayers(14)
            .submit().join()
        Assertions.assertEquals(14, getCurrentGroupConfig().maxPlayers)
    }

    @Test
    fun updatePriorityWithNegativeNumber() {
        processGroupService.createUpdateRequest(existingGroup)
            .setStartPriority(-5)
            .submit().join()
        Assertions.assertEquals(-5, getCurrentGroupConfig().startPriority)
    }

    @Test
    fun updatePriorityTest() {
        processGroupService.createUpdateRequest(existingGroup)
            .setStartPriority(22)
            .submit().join()
        Assertions.assertEquals(22, getCurrentGroupConfig().startPriority)
    }


    private fun getCurrentGroupConfig(): AbstractProcessTemplateConfiguration {
        return this.processGroupService.findByName(existingGroup.getName()).join().toConfiguration()
    }


}