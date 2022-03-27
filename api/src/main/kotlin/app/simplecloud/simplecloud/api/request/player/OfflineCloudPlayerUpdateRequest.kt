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
import app.simplecloud.simplecloud.api.request.permission.PermissionPlayerUpdateRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 18:38
 * @author Frederick Baier
 */
interface OfflineCloudPlayerUpdateRequest : PermissionPlayerUpdateRequest {

    override fun clearPermissions(): OfflineCloudPlayerUpdateRequest

    override fun addPermission(permission: Permission): OfflineCloudPlayerUpdateRequest

    override fun removePermission(permissionString: String): OfflineCloudPlayerUpdateRequest

    override fun clearPermissionGroups(): OfflineCloudPlayerUpdateRequest

    override fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): OfflineCloudPlayerUpdateRequest

    override fun removePermissionGroup(groupName: String): OfflineCloudPlayerUpdateRequest

    /**
     * Sets the display name of the player
     */
    fun setDisplayName(name: String): OfflineCloudPlayerUpdateRequest

    /**
     * Sets the web config
     */
    fun setWebConfig(webConfig: PlayerWebConfig): OfflineCloudPlayerUpdateRequest

}