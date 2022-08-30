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

package app.simplecloud.simplecloud.node.api.permission.group

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 20.08.22
 * Time: 21:42
 * @author Frederick Baier
 *
 */
abstract class PermissionGroupDeleteBaseTest : PermissionGroupServiceBaseTest() {

    private lateinit var existingGroup: PermissionGroup

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.existingGroup =
            this.groupService.createCreateRequest(createPermissionGroupConfiguration("ExistingGroup")).submit().join()
    }

    @Test
    fun doNothing_GroupStillExsist() {
        Assertions.assertEquals(1, this.groupService.findAll().join().size)
    }

    @Test
    fun deleteGroup_groupCountWillBe0() = runBlocking {
        deleteGroup(existingGroup)
        assertExistingGroupCount(0)
    }

    @Test
    fun createTwoGroups_deleteOne_oneWillStillExist() = runBlocking {
        groupService.createCreateRequest(createPermissionGroupConfiguration("Test")).submit().await()
        deleteGroup(existingGroup)
        assertExistingGroupCount(1)
    }

    private fun assertExistingGroupCount(count: Int) {
        Assertions.assertEquals(count, this.groupService.findAll().join().size)
    }

    private fun deleteGroup(existingGroup: PermissionGroup) {
        this.groupService.createDeleteRequest(existingGroup).submit().join()
    }

}