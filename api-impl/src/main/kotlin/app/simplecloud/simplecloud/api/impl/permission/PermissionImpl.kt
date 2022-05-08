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

import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration


class PermissionImpl constructor(
    private val configuration: PermissionConfiguration
) : Permission {

    private val rawString = this.configuration.permissionString.lowercase()
    private val targetProcessGroup = this.configuration.targetProcessGroup

    override fun isActive(): Boolean {
        return this.configuration.active
    }

    override fun getRawString(): String {
        return this.configuration.permissionString
    }

    override fun getExpireTimestamp(): Long {
        return this.configuration.expiresAtTimestamp
    }

    override fun toConfiguration(): PermissionConfiguration {
        return this.configuration
    }

    override fun matches(permissionString: String, processGroup: String?): Boolean {
        if (isExpired()) return false
        if (this.targetProcessGroup == null || this.targetProcessGroup == processGroup)
            return matchesPermission(permissionString)
        return false
    }

    private fun isExpired(): Boolean {
        val expiresAtTimestamp = this.configuration.expiresAtTimestamp
        return System.currentTimeMillis() > expiresAtTimestamp && expiresAtTimestamp != -1L
    }

    private fun matchesPermission(permission: String): Boolean {
        val endsWithStar = this.rawString.endsWith(".*")
        return if (endsWithStar) {
            matchesWithStarAtTheEnd(permission)
        } else {
            isPermissionToCheckEqualToThisPermission(permission)
        }
    }

    private fun isPermissionToCheckEqualToThisPermission(permission: String): Boolean {
        return permission == this.rawString
    }

    private fun matchesWithStarAtTheEnd(permission: String): Boolean {
        val checkPermissionComponents = permission.split(".")
        val thisPermissionComponents = this.rawString.split(".")

        val isPermissionToCheckLongEnough = checkPermissionComponents.size >= thisPermissionComponents.size
        return if (isPermissionToCheckLongEnough) {
            areAllPermissionComponentsEqual(checkPermissionComponents, thisPermissionComponents)
        } else {
            false
        }
    }

    private fun areAllPermissionComponentsEqual(
        checkPermissionComponents: List<String>,
        thisPermissionComponents: List<String>
    ): Boolean {
        val thisPermissionComponentsWithoutStar = thisPermissionComponents.dropLast(1)
        val checkComponentsWithLimit = checkPermissionComponents.take(thisPermissionComponentsWithoutStar.size)
        return checkComponentsWithLimit == thisPermissionComponentsWithoutStar
    }

}