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

package app.simplecloud.simplecloud.node.api

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.image.ImageImpl
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 19.08.22
 * Time: 09:12
 * @author Frederick Baier
 *
 */
abstract class ProcessTemplateUpdateBaseTest : ProcessTemplateServiceBaseTest() {

    private lateinit var existingTemplate: ProcessTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()
        existingTemplate = this.templateService.createCreateRequest(createLobbyTemplateConfiguration()).submit().join()
    }

    @Test
    fun updateImageTest() {
        val imageName = RandomStringUtils.randomAlphabetic(16)
        this.templateService.createUpdateRequest(existingTemplate)
            .setImage(ImageImpl.fromName(imageName))
            .submit().join()
        Assertions.assertEquals(imageName, getCurrentTemplateConfig().imageName)
    }

    @Test
    fun updateMaintenanceTest() {
        this.templateService.createUpdateRequest(existingTemplate)
            .setMaintenance(true)
            .submit().join()
        Assertions.assertEquals(true, getCurrentTemplateConfig().maintenance)
    }

    @Test
    fun updateStateUpdatingTest() {
        this.templateService.createUpdateRequest(existingTemplate)
            .setStateUpdating(true)
            .submit().join()
        Assertions.assertEquals(true, getCurrentTemplateConfig().stateUpdating)
    }

    @Test
    fun updateMaxMemory_negativeInput_willFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                templateService.createUpdateRequest(existingTemplate)
                    .setMaxMemory(-7)
                    .submit().await()
            }
        }
    }

    @Test
    fun updateMemory_withTooLowInput_willFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                templateService.createUpdateRequest(existingTemplate)
                    .setMaxMemory(200)
                    .submit().await()
            }
        }
    }

    @Test
    fun updateMemory_with256MB() {
        templateService.createUpdateRequest(existingTemplate)
            .setMaxMemory(256)
            .submit().join()
    }

    @Test
    fun updatePermissionTest() {
        val permission = RandomStringUtils.randomAlphabetic(16)
        templateService.createUpdateRequest(existingTemplate)
            .setJoinPermission(permission)
            .submit().join()
        Assertions.assertEquals(permission, getCurrentTemplateConfig().joinPermission)
    }

    @Test
    fun updateMaxPlayers_withTooLowInput_willFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                templateService.createUpdateRequest(existingTemplate)
                    .setMaxPlayers(-2)
                    .submit().await()
            }
        }
    }

    @Test
    fun updateMaxPlayers_withInfinitePlayers() {
        templateService.createUpdateRequest(existingTemplate)
            .setMaxPlayers(-1)
            .submit().join()
        Assertions.assertEquals(-1, getCurrentTemplateConfig().maxPlayers)
    }

    @Test
    fun updateMaxPlayersTest() {
        templateService.createUpdateRequest(existingTemplate)
            .setMaxPlayers(14)
            .submit().join()
        Assertions.assertEquals(14, getCurrentTemplateConfig().maxPlayers)
    }

    @Test
    fun updatePriorityWithNegativeNumber() {
        templateService.createUpdateRequest(existingTemplate)
            .setStartPriority(-5)
            .submit().join()
        Assertions.assertEquals(-5, getCurrentTemplateConfig().startPriority)
    }

    @Test
    fun updatePriorityTest() {
        templateService.createUpdateRequest(existingTemplate)
            .setStartPriority(22)
            .submit().join()
        Assertions.assertEquals(22, getCurrentTemplateConfig().startPriority)
    }


    private fun getCurrentTemplateConfig(): AbstractProcessTemplateConfiguration {
        return this.templateService.findByName(existingTemplate.getName()).join().toConfiguration()
    }

}