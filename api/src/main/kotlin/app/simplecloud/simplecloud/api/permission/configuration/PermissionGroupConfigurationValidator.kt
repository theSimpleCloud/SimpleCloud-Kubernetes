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

package app.simplecloud.simplecloud.api.permission.configuration

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.toFutureList
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.service.PermissionGroupService
import app.simplecloud.simplecloud.api.utils.DefaultNameRequirement

/**
 * Date: 22.08.22
 * Time: 08:38
 * @author Frederick Baier
 *
 */
class PermissionGroupConfigurationValidator(
    private val groupConfiguration: PermissionGroupConfiguration,
    private val groupService: PermissionGroupService,
) {

    suspend fun validate() {
        DefaultNameRequirement.checkName(this.groupConfiguration.name)
        checkPermissionConfigurations()
        checkForRecursion()
    }

    private fun checkPermissionConfigurations() {
        val permissions = this.groupConfiguration.permissions
        for (permission in permissions) {
            PermissionConfigurationValidator(permission).validate()
        }
    }

    private suspend fun checkForRecursion() {
        if (doesAnySubGroupHasThisGroupAsDependency())
            throw GroupRecursionException("Recursion detected within group ${this.groupConfiguration.name}")
    }

    private suspend fun doesAnySubGroupHasThisGroupAsDependency(): Boolean {
        val thisGroupPermission = "group.${this.groupConfiguration.name}"
        val subGroups = getSubGroups()

        return subGroups.any { it.hasPermission(thisGroupPermission).await() }
    }

    private fun getSubGroupNames(): List<String> {
        val permissions = this.groupConfiguration.permissions
        return permissions.filter { it.permissionString.startsWith("group.") }
            .map { it.permissionString.replaceFirst("group.", "") }
    }

    private suspend fun getSubGroups(): List<PermissionGroup> {
        val subGroupNames = getSubGroupNames()
        return subGroupNames.map { this.groupService.findByName(it) }.toFutureList().await()
    }

    class GroupRecursionException(message: String) : Exception(message)

}