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

package app.simplecloud.simplecloud.node.api.permission

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * Date: 20.08.22
 * Time: 21:42
 * @author Frederick Baier
 *
 */
abstract class PermissionGroupUpdateBaseTest : PermissionGroupServiceBaseTest() {

    private lateinit var existingGroup: PermissionGroup

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.existingGroup =
            this.groupService.createCreateRequest(createPermissionGroupConfiguration("ExistingGroup")).submit().join()
    }

    @Test
    fun updateWithoutEditing_willNotFail() {
        this.groupService.createUpdateRequest(existingGroup).submit().join()
    }

    @ParameterizedTest
    @ValueSource(ints = [-5, -1, 65, 2463, 2, 0])
    fun updatePriorityTest(prioriy: Int) {
        this.groupService.createUpdateRequest(existingGroup)
            .setPriority(prioriy)
            .submit().join()

        Assertions.assertEquals(prioriy, fetchPermissionGroupAgain().getPriority())
    }

    @ParameterizedTest
    @MethodSource("permissions")
    fun addPermissionTest(permission: PermissionConfiguration) {
        this.groupService.createUpdateRequest(existingGroup)
            .addPermission(this.permissionFactory.create(permission))
            .submit().join()

        val updatedGroup = fetchPermissionGroupAgain()
        val allPermissionConfigurations = updatedGroup.getPermissions().map { it.toConfiguration() }
        Assertions.assertTrue(allPermissionConfigurations.contains(permission))
    }

    @ParameterizedTest
    @ValueSource(strings = ["#", "test.command.#", "üman.*", "eßa", "test,", "%2", "ös", "msco..*", "tes.*.sw", "*f", "test.", ".test"])
    fun addInvalidPermission_willFail(permissionString: String) {
        val configuration = PermissionConfiguration(
            permissionString,
            true,
            -1L,
            null
        )
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                groupService.createUpdateRequest(existingGroup)
                    .addPermission(permissionFactory.create(configuration))
                    .submit().await()
            }
        }
    }

    @Test
    fun addValidPermissionGroupTest() {
        val otherName = "Other"
        val otherGroup = givenPermissionGroup(otherName)

        this.groupService.createUpdateRequest(existingGroup)
            .addPermissionGroup(otherGroup, -1L)
            .submit().join()

        val permissionGroup = fetchPermissionGroupAgain()
        Assertions.assertTrue(permissionGroup.hasTopLevelGroup(otherName))

    }

    @Test
    fun removePermissionGroupTest() {
        val otherGroup = givenPermissionGroup("Other")
        addOtherGroupToExistingGroup(otherGroup)

        this.groupService.createUpdateRequest(existingGroup)
            .removePermissionGroup(otherGroup.getName())
            .submit().join()

        val permissionGroup = fetchPermissionGroupAgain()
        Assertions.assertFalse(permissionGroup.hasTopLevelGroup(otherGroup.getName()))
    }

    @Test
    fun clearPermissionGroupTest() {
        val otherGroup = givenPermissionGroup("Other")
        val other2Group = givenPermissionGroup("Other2")
        addOtherGroupToExistingGroup(otherGroup)
        addOtherGroupToExistingGroup(other2Group)

        this.groupService.createUpdateRequest(existingGroup)
            .clearPermissionGroups()
            .submit().join()

        val permissionGroup = fetchPermissionGroupAgain()
        Assertions.assertEquals(0, permissionGroup.getTopLevelPermissionGroups().join().size)
    }

    @Test
    fun removePermissionTest() {
        val permissionString = "test.*"
        addPermissionToExistingGroup(permissionString)

        this.groupService.createUpdateRequest(existingGroup)
            .removePermission(permissionString)
            .submit().join()


        val permissionGroup = fetchPermissionGroupAgain()
        Assertions.assertEquals(0, permissionGroup.getPermissions().size)
    }

    private fun addPermissionToExistingGroup(permissionString: String) {
        this.groupService.createUpdateRequest(existingGroup)
            .addPermission(
                this.permissionFactory.create(
                    PermissionConfiguration(permissionString, true, -1L, null)
                )
            ).submit().join()
    }

    private fun addOtherGroupToExistingGroup(otherGroup: PermissionGroup) {
        this.groupService.createUpdateRequest(existingGroup)
            .addPermissionGroup(otherGroup, -1L)
            .submit().join()
    }

    private fun givenPermissionGroup(name: String): PermissionGroup {
        val configuration = createPermissionGroupConfiguration(name)
        return this.groupService.createCreateRequest(configuration).submit().join()
    }

    private fun fetchPermissionGroupAgain(): PermissionGroup {
        return this.groupService.findByName(this.existingGroup.getName()).join()
    }

    companion object {
        @JvmStatic
        fun permissions(): List<PermissionConfiguration> {
            return listOf(
                PermissionConfiguration("*", true, -1, null),
                PermissionConfiguration("minecraft.*", true, -1, null),
                PermissionConfiguration("test.mypermission", true, -1, null),
            )
        }
    }

}