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
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 19.08.22
 * Time: 09:12
 * @author Frederick Baier
 *
 */
abstract class ProcessTemplateDeleteBaseTest : ProcessTemplateServiceBaseTest() {

    private lateinit var existingTemplate: ProcessTemplate

    private var prevRegisteredTemplateCount: Int = 0

    @BeforeEach
    override fun setUp() {
        super.setUp()
        prevRegisteredTemplateCount = this.templateService.findAll().join().size
        existingTemplate = this.templateService.createCreateRequest(createLobbyTemplateConfiguration()).submit().join()
    }

    @Test
    fun doNothing_groupExists() = runBlocking {
        assertExistingGroupCount(prevRegisteredTemplateCount + 1)
    }

    @Test
    fun deleteTemplate_groupCountWillBe0() = runBlocking {
        deleteTemplate(existingTemplate)
        assertExistingGroupCount(prevRegisteredTemplateCount)
    }

    @Test
    fun createTwoGroups_deleteOne_oneWillStillExist() = runBlocking {
        templateService.createCreateRequest(createLobbyTemplateConfiguration("Test")).submit().await()
        deleteTemplate(existingTemplate)
        assertExistingGroupCount(prevRegisteredTemplateCount + 1)
    }

    private fun assertExistingGroupCount(count: Int) {
        Assertions.assertEquals(count, this.templateService.findAll().join().size)
    }

    private suspend fun deleteTemplate(template: ProcessTemplate) {
        templateService.createDeleteRequest(template).submit().await()
    }

}