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

package app.simplecloud.simplecloud.api.request.player

import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.player.PlayerWebConfig


/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 18:38
 * @author Frederick Baier
 */
interface CloudPlayerUpdateRequest : OfflineCloudPlayerUpdateRequest {

    override fun clearPermissions(): CloudPlayerUpdateRequest

    override fun addPermission(permission: Permission): CloudPlayerUpdateRequest

    override fun removePermission(permissionString: String): CloudPlayerUpdateRequest

    override fun clearPermissionGroups(): CloudPlayerUpdateRequest

    override fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): CloudPlayerUpdateRequest

    override fun removePermissionGroup(groupName: String): CloudPlayerUpdateRequest

    override fun setDisplayName(name: String): CloudPlayerUpdateRequest

    override fun setWebConfig(webConfig: PlayerWebConfig): CloudPlayerUpdateRequest

}