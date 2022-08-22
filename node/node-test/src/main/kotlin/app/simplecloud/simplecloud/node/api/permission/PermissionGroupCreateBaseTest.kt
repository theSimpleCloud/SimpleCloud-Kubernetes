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
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
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
abstract class PermissionGroupCreateBaseTest : PermissionGroupServiceBaseTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun createValidPermissionGroup_willNotFail() {
        this.groupService.createCreateRequest(createPermissionGroupConfiguration()).submit().join()
    }

    @ParameterizedTest
    @MethodSource("validPermissionGroups")
    fun createPermissionGroup_WillHaveSameConfigurationAsSubmitted(configuration: PermissionGroupConfiguration) {
        this.groupService.createCreateRequest(configuration).submit().join()
        val permissionGroup = this.groupService.findByName(configuration.name).join()
        Assertions.assertEquals(configuration, permissionGroup.toConfiguration())
    }


    @Test
    fun createGroupWithEmptyName_WillFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                groupService.createCreateRequest(createPermissionGroupConfiguration("")).submit().await()
            }
        }
    }

    @Test
    fun createGroupWithTooShortName_WillFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                groupService.createCreateRequest(createPermissionGroupConfiguration("s")).submit().await()
            }
        }
    }

    @Test
    fun createGroupWithSameNameTwice_willFail(): Unit = runBlocking {
        groupService.createCreateRequest(createPermissionGroupConfiguration("Test")).submit().await()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                groupService.createCreateRequest(createPermissionGroupConfiguration("Test")).submit().await()
            }
        }
    }

    @Test
    fun createGroupWithEmptyPermission_willFail(): Unit = runBlocking {
        val configuration = PermissionConfiguration(
            "",
            true,
            -1L,
            null
        )
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                groupService.createCreateRequest(createPermissionGroupConfigurationWithPermission(configuration))
                    .submit().await()
            }
        }
    }

    @Test
    fun createGroupWithEmptyTargetGroup_willFail(): Unit = runBlocking {
        val configuration = PermissionConfiguration(
            "test",
            true,
            -1L,
            ""
        )
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                groupService.createCreateRequest(createPermissionGroupConfigurationWithPermission(configuration))
                    .submit().await()
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["#", "test.command.#", "üman.*", "eßa", "test,", "%2", "ös", "msco..*", "tes.*.sw", "*f", "test.", ".test"])
    fun createGroupWithInvalidPermission_WillFail(permissionString: String): Unit = runBlocking {
        val configuration = PermissionConfiguration(
            permissionString,
            true,
            -1L,
            null
        )
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                groupService.createCreateRequest(createPermissionGroupConfigurationWithPermission(configuration))
                    .submit().await()
            }
        }
    }

    companion object {
        @JvmStatic
        private fun validPermissionGroups(): List<PermissionGroupConfiguration> {
            return listOf(
                PermissionGroupConfiguration(
                    "Test",
                    4,
                    listOf(
                        PermissionConfiguration("*", true, -1L, null)
                    )
                ),
                PermissionGroupConfiguration(
                    "Test",
                    -6,
                    listOf(
                        PermissionConfiguration("minecraft.*", false, 5990, "Lobby")
                    )
                ),
                PermissionGroupConfiguration(
                    "es",
                    -6,
                    listOf(
                        PermissionConfiguration("minecraft.*", false, 5990, "Lobby")
                    )
                ),
                PermissionGroupConfiguration(
                    "es",
                    -6,
                    listOf(
                        PermissionConfiguration("minecraft.test.command", false, 5990, "Lobby")
                    )
                )
            )
        }
    }


}