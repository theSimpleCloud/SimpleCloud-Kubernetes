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

package app.simplecloud.simplecloud.api.internal.request.player

import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.request.player.CloudPlayerUpdateRequest

/**
 * Date: 13.01.22
 * Time: 18:52
 * @author Frederick Baier
 *
 */
interface InternalCloudPlayerUpdateRequest : CloudPlayerUpdateRequest {

    override fun clearPermissions(): InternalCloudPlayerUpdateRequest

    override fun addPermission(permission: Permission): InternalCloudPlayerUpdateRequest

    override fun removePermission(permissionString: String): InternalCloudPlayerUpdateRequest

    override fun clearPermissionGroups(): InternalCloudPlayerUpdateRequest

    override fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): InternalCloudPlayerUpdateRequest

    override fun removePermissionGroup(groupName: String): InternalCloudPlayerUpdateRequest

    override fun setDisplayName(name: String): InternalCloudPlayerUpdateRequest

    override fun setWebConfig(webConfig: PlayerWebConfig): InternalCloudPlayerUpdateRequest

    fun setConnectedProxyName(name: String): InternalCloudPlayerUpdateRequest

    fun setConnectedServerName(name: String): InternalCloudPlayerUpdateRequest

}