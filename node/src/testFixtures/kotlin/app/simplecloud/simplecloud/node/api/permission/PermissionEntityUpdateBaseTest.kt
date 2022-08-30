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

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.request.permission.PermissionEntityUpdateRequest
import app.simplecloud.simplecloud.node.util.TestPermissionGroupProvider
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * Date: 29.08.22
 * Time: 11:41
 * @author Frederick Baier
 *
 */
abstract class PermissionEntityUpdateBaseTest : TestPermissionGroupProvider {

    private lateinit var existingEntity: PermissionEntity

    abstract fun fetchDefaultPermissionEntity(): PermissionEntity

    abstract fun createUpdateRequest(entity: PermissionEntity): PermissionEntityUpdateRequest

    abstract override fun getCloudAPI(): CloudAPI

    @BeforeEach
    open fun setUp() {
        this.existingEntity = fetchDefaultPermissionEntity()
    }

    @Test
    fun updateWithoutEditing_willNotFail() {
        createUpdateRequest(existingEntity).submit().join()
    }

    @ParameterizedTest
    @MethodSource("permissions")
    fun addPermissionTest(permission: PermissionConfiguration) {
        createUpdateRequest(existingEntity)
            .addPermission(getCloudAPI().getPermissionFactory().create(permission))
            .submit().join()

        val updatedEntity = fetchDefaultPermissionEntity()
        val allPermissionConfigurations = updatedEntity.getPermissions().map { it.toConfiguration() }
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
                createUpdateRequest(existingEntity)
                    .addPermission(getCloudAPI().getPermissionFactory().create(configuration))
                    .submit().await()
            }
        }
    }

    @Test
    fun addValidPermissionGroupTest() {
        val otherName = "Other"
        val otherGroup = givenPermissionGroup(otherName)

        createUpdateRequest(existingEntity)
            .addPermissionGroup(otherGroup, -1L)
            .submit().join()

        val permissionGroup = fetchDefaultPermissionEntity()
        Assertions.assertTrue(permissionGroup.hasTopLevelGroup(otherName))
    }

    @Test
    fun removePermissionGroupTest() {
        val otherGroup = givenPermissionGroup("Other")
        addOtherGroupToExistingEntity(otherGroup)

        createUpdateRequest(this.existingEntity)
            .removePermissionGroup(otherGroup.getName())
            .submit().join()

        val permissionGroup = fetchDefaultPermissionEntity()
        Assertions.assertFalse(permissionGroup.hasTopLevelGroup(otherGroup.getName()))
    }

    @Test
    fun clearPermissionGroupTest() {
        val otherGroup = givenPermissionGroup("Other")
        val other2Group = givenPermissionGroup("Other2")
        addOtherGroupToExistingEntity(otherGroup)
        addOtherGroupToExistingEntity(other2Group)

        createUpdateRequest(existingEntity)
            .clearPermissionGroups()
            .submit().join()

        val permissionGroup = fetchDefaultPermissionEntity()
        Assertions.assertEquals(0, permissionGroup.getTopLevelPermissionGroups().join().size)
    }

    @Test
    fun removePermissionTest() {
        val permissionString = "test.*"
        addPermissionToExistingEntity(permissionString)

        createUpdateRequest(existingEntity)
            .removePermission(permissionString)
            .submit().join()


        val permissionGroup = fetchDefaultPermissionEntity()
        Assertions.assertEquals(0, permissionGroup.getPermissions().size)
    }

    private fun addPermissionToExistingEntity(permissionString: String) {
        createUpdateRequest(existingEntity)
            .addPermission(
                getCloudAPI().getPermissionFactory().create(
                    PermissionConfiguration(permissionString, true, -1L, null)
                )
            ).submit().join()
    }

    private fun addOtherGroupToExistingEntity(otherGroup: PermissionGroup) {
        createUpdateRequest(this.existingEntity)
            .addPermissionGroup(otherGroup, -1L)
            .submit().join()
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