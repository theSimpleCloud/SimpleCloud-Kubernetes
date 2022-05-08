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

package app.simplecloud.simplecloud.api.impl.permission

import app.simplecloud.simplecloud.api.future.exception.FutureOriginException
import app.simplecloud.simplecloud.api.impl.permission.group.PermissionGroupFactoryImpl
import app.simplecloud.simplecloud.api.impl.request.permission.PermissionGroupUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.repository.PermissionGroupRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletionException

/**
 * Date: 23.03.22
 * Time: 08:54
 * @author Frederick Baier
 *
 */
class PermissionGroupUpdateRequestTest {

    private lateinit var permissionGroupService: InternalPermissionGroupService
    private lateinit var permissionGroupRepository: PermissionGroupRepository


    @BeforeEach
    fun beforeEach() {
        this.permissionGroupRepository = PermissionGroupRepositoryImpl()
        val permissionFactory = PermissionFactoryImpl()
        this.permissionGroupService = PermissionGroupServiceImpl(
            this.permissionGroupRepository,
            PermissionGroupFactoryImpl(permissionFactory),
            permissionFactory
        )
    }

    @Test
    fun simple_priority_changed_test() {
        val groupConfiguration = PermissionGroupConfiguration("Test", 5, emptyList())
        this.permissionGroupRepository.save("Test", groupConfiguration)

        val permissionGroup = this.permissionGroupService.findByName("Test").join()
        val updateRequest = this.permissionGroupService.createUpdateRequest(permissionGroup)
        updateRequest.setPriority(2)
        updateRequest.submit().join()

        val changedGroup = this.permissionGroupService.findByName("Test").join()
        Assertions.assertEquals(2, changedGroup.getPriority())
    }

    @Test
    fun self_depend_test() {
        val groupConfiguration = PermissionGroupConfiguration(
            "test",
            5,
            listOf(
                PermissionConfiguration("group.test", true, -1L, null)
            )
        )
        this.permissionGroupRepository.save("test", groupConfiguration)

        val permissionGroup = this.permissionGroupService.findByName("test").join()
        val updateRequest = this.permissionGroupService.createUpdateRequest(permissionGroup)
        Assertions.assertThrows(PermissionGroupUpdateRequestImpl.GroupRecursionException::class.java) {
            try {
                updateRequest.submit().join()
            } catch (e: Exception) {
                throw unpackException(e)
            }
        }
    }

    @Test
    fun recursion_test() {
        val groupConfiguration = PermissionGroupConfiguration(
            "admin",
            5,
            listOf(
                PermissionConfiguration("group.builder", true, -1L, null)
            )
        )

        val groupConfiguration2 = PermissionGroupConfiguration(
            "builder",
            5,
            listOf(
                PermissionConfiguration("group.admin", true, -1L, null)
            )
        )
        this.permissionGroupRepository.save("admin", groupConfiguration)
        this.permissionGroupRepository.save("builder", groupConfiguration2)

        val permissionGroup = this.permissionGroupService.findByName("admin").join()
        val updateRequest = this.permissionGroupService.createUpdateRequest(permissionGroup)

        Assertions.assertThrows(PermissionGroupUpdateRequestImpl.GroupRecursionException::class.java) {
            try {
                updateRequest.submit().join()
            } catch (e: Exception) {
                throw unpackException(e)
            }
        }
    }

    @Test
    fun three_recursion_test() {
        val groupConfiguration = PermissionGroupConfiguration(
            "admin",
            5,
            listOf(
                PermissionConfiguration("group.builder", true, -1L, null)
            )
        )

        val groupConfiguration2 = PermissionGroupConfiguration(
            "builder",
            5,
            listOf(
                PermissionConfiguration("group.mod", true, -1L, null)
            )
        )

        val groupConfiguration3 = PermissionGroupConfiguration(
            "mod",
            5,
            listOf(
                PermissionConfiguration("group.admin", true, -1L, null)
            )
        )
        this.permissionGroupRepository.save("admin", groupConfiguration)
        this.permissionGroupRepository.save("builder", groupConfiguration2)
        this.permissionGroupRepository.save("mod", groupConfiguration3)

        val permissionGroup = this.permissionGroupService.findByName("admin").join()
        val updateRequest = this.permissionGroupService.createUpdateRequest(permissionGroup)

        Assertions.assertThrows(PermissionGroupUpdateRequestImpl.GroupRecursionException::class.java) {
            try {
                updateRequest.submit().join()
            } catch (e: Exception) {
                throw unpackException(e)
            }
        }
    }

    private fun unpackException(ex: Throwable): Throwable {
        if (ex is CompletionException) {
            return unpackException(ex.cause!!)
        }
        if (ex is InvocationTargetException) {
            return unpackException(ex.cause!!)
        }
        if (ex is FutureOriginException) {
            return unpackException(ex.cause!!)
        }
        return ex
    }


}