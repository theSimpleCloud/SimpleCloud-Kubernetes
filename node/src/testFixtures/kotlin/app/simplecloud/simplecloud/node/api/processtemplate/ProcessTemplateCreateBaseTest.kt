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

package app.simplecloud.simplecloud.node.api.processtemplate

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.node.assertContains
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Date: 19.08.22
 * Time: 09:12
 * @author Frederick Baier
 *
 */
abstract class ProcessTemplateCreateBaseTest : ProcessTemplateServiceBaseTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun normalTemplateCreate_willNotFail() = runBlocking {
        val prevGroupCount = templateService.findAll().await().size
        val configuration = createLobbyTemplateConfiguration()
        createTemplate(configuration)
        assertTemplateCount(prevGroupCount + 1)
        assertClusterContainsTemplate(configuration)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "e", "a"])
    fun createTemplateWithInvalidName_willFail(name: String) {
        val configuration = createLobbyTemplateConfiguration(name)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createTemplate(configuration)
            }
        }
    }

    @Test
    fun createTwoDifferentTemplates_willNotFail(): Unit = runBlocking {
        val configuration = createLobbyTemplateConfiguration("Lobby")
        val configuration2 = createLobbyTemplateConfiguration("Test")
        createTemplate(configuration)
        createTemplate(configuration2)
    }

    @Test
    fun createSameTemplateTwice_willFail(): Unit = runBlocking {
        val configuration = createLobbyTemplateConfiguration()
        createTemplate(configuration)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createTemplate(configuration)
            }
        }
    }

    @Test
    fun createTemplate_withNegativeMaxMemory_willFail(): Unit = runBlocking {
        val configuration = createTemplateConfigWithMemory(-5)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createTemplate(configuration)
            }
        }
    }

    @Test
    fun createTemplate_withTooLowMaxMemory_willFail(): Unit = runBlocking {
        val configuration = createTemplateConfigWithMemory(200)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createTemplate(configuration)
            }
        }
    }

    @Test
    fun createTemplate_withMaxMemory(): Unit = runBlocking {
        val configuration = createTemplateConfigWithMemory(512)
        runBlocking {
            createTemplate(configuration)
        }
    }

    @Test
    fun createTemplate_withTooLowMaxPlayers_willFail(): Unit = runBlocking {
        val configuration = createTemplateConfigWithMaxPlayers(-5)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createTemplate(configuration)
            }
        }
    }

    @Test
    fun createTemplate_withInfiniteMaxPlayers(): Unit = runBlocking {
        val configuration = createTemplateConfigWithMaxPlayers(-1)
        createTemplate(configuration)
    }

    @Test
    fun createTemplate_withNormalMaxPlayers(): Unit = runBlocking {
        val configuration = createTemplateConfigWithMaxPlayers(22)
        createTemplate(configuration)
    }

    @Test
    fun createProxyTemplate_willNotFail(): Unit = runBlocking {
        val configuration = createProxyTemplate()
        createTemplate(configuration)
    }


    private fun assertClusterContainsTemplate(groupConfiguration: AbstractProcessTemplateConfiguration) {
        val allTemplatesConfigs = templateService.findAll().join().map { it.toConfiguration() }
        assertContains(allTemplatesConfigs, groupConfiguration)
    }

    private fun assertTemplateCount(count: Int) {
        Assertions.assertEquals(count, templateService.findAll().join().size)
    }

    private suspend fun createTemplate(groupConfiguration: AbstractProcessTemplateConfiguration) {
        templateService.createCreateRequest(groupConfiguration).submit().await()
    }

    private fun createTemplateConfigWithMaxPlayers(maxPlayers: Int): ProxyProcessTemplateConfiguration {
        return createTemplateConfigWithMemoryAndMaxPlayers(512, maxPlayers)
    }

    private fun createTemplateConfigWithMemory(maxMemory: Int): ProxyProcessTemplateConfiguration {
        return createTemplateConfigWithMemoryAndMaxPlayers(maxMemory, 20)
    }

    private fun createTemplateConfigWithMemoryAndMaxPlayers(
        maxMemory: Int,
        maxPlayers: Int,
    ): ProxyProcessTemplateConfiguration {
        return ProxyProcessTemplateConfiguration(
            "ProxyN",
            maxMemory,
            maxPlayers,
            false,
            "Test",
            false,
            0,
            null,
            true
        )
    }

    private fun createProxyTemplate(): ProxyProcessTemplateConfiguration {
        return ProxyProcessTemplateConfiguration(
            "ProxyN",
            512,
            20,
            false,
            "Test",
            false,
            0,
            null,
            true
        )
    }

}