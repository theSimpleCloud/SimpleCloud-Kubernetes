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

/**
 * Date: 21.08.22
 * Time: 12:24
 * @author Frederick Baier
 *
 */
class PermissionConfigurationValidator(
    private val permissionConfiguration: PermissionConfiguration,
) {

    private val allowedPermissionStringCharacters = "abcdefghijklmnopqrstuvwxyz0123456789.*".toCharArray()

    fun validate() {
        checkTargetGroup()
        checkPermissionString()
    }

    private fun checkPermissionString() {
        val permissionString = this.permissionConfiguration.permissionString
        require(permissionString != "") { "permissionString cannot be empty" }
        checkOnlyAllowedCharactersInPermissionString(permissionString)
        checkValidStarPlacement(permissionString)
        checkPermissionStringDoesNotStartWithDot(permissionString)
        checkPermissionStringDoesNotEndWithDot(permissionString)
        checkForDoubleDotOccurrence(permissionString)
    }

    private fun checkPermissionStringDoesNotStartWithDot(permissionString: String) {
        if (permissionString.startsWith("."))
            throw IllegalArgumentException("permissionString must not start with '.'")
    }

    private fun checkPermissionStringDoesNotEndWithDot(permissionString: String) {
        if (permissionString.endsWith("."))
            throw IllegalArgumentException("permissionString must not end with '.'")
    }

    private fun checkForDoubleDotOccurrence(permissionString: String) {
        if (permissionString.contains(".."))
            throw IllegalArgumentException("permissionString must not contain two following dots: $permissionString")
    }

    private fun checkValidStarPlacement(permissionString: String) {
        permissionString.forEachIndexed { index, char ->
            if (char == '*' && index != permissionString.lastIndex) {
                throw IllegalArgumentException("Invalid occurrence of '*' in permissionString: $permissionString")
            }
        }
    }

    private fun checkOnlyAllowedCharactersInPermissionString(permissionString: String) {
        val isCharacterAllowed: (Char) -> Boolean = { allowedPermissionStringCharacters.contains(it) }
        for (char in permissionString) {
            if (!isCharacterAllowed(char)) {
                throw IllegalArgumentException("Permission string must contain only alphanumeric characters or '.' but was $permissionString")
            }
        }
    }

    private fun checkTargetGroup() {
        val targetProcessGroup = this.permissionConfiguration.targetProcessGroup
        require(targetProcessGroup != "") { "targetProcessGroup cannot be empty" }
    }

}