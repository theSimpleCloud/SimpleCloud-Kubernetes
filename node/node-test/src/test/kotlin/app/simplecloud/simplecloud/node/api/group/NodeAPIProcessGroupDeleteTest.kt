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
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 11.05.22
 * Time: 18:16
 * @author Frederick Baier
 *
 */
class NodeAPIProcessGroupDeleteTest : NodeAPIProcessGroupTest() {


    private lateinit var existingGroup: CloudProcessGroup

    @BeforeEach
    override fun setUp() {
        super.setUp()
        existingGroup = this.processGroupService.createCreateRequest(createLobbyGroupConfiguration()).submit().join()
    }

    @Test
    fun doNothing_groupExists() = runBlocking {
        assertExistingGroupCount(1)
    }

    @Test
    fun deleteGroup_groupCountWillBe0() = runBlocking {
        deleteGroup(existingGroup)
        assertExistingGroupCount(0)
    }

    @Test
    fun createTwoGroups_deleteOne_oneWillStillExist() = runBlocking {
        processGroupService.createCreateRequest(createLobbyGroupConfiguration("Test")).submit().await()
        deleteGroup(existingGroup)
        assertExistingGroupCount(1)
    }

    private fun assertExistingGroupCount(count: Int) {
        Assertions.assertEquals(count, this.processGroupService.findAll().join().size)
    }

    private suspend fun deleteGroup(group: CloudProcessGroup) {
        processGroupService.createDeleteRequest(group).submit().await()
    }


}