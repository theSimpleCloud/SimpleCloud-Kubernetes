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

package app.simplecloud.simplecloud.api.impl.player

import app.simplecloud.simplecloud.api.impl.request.player.OfflineCloudPlayerUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.PermissionPlayer
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerConnection
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.request.player.OfflineCloudPlayerUpdateRequest
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 17:57
 * @author Frederick Baier
 *
 */
open class OfflineCloudPlayerImpl constructor(
    private val configuration: OfflineCloudPlayerConfiguration,
    private val cloudPlayerService: InternalCloudPlayerService,
    private val permissionFactory: Permission.Factory,
    permissionPlayerFactory: PermissionPlayer.Factory
) : OfflineCloudPlayer {

    private val permissionPlayer = permissionPlayerFactory.create(this.configuration.permissionPlayerConfiguration)

    private val lastPlayerConnection = PlayerConnectionImpl(this.configuration.lastPlayerConnection)

    override fun getName(): String {
        return this.configuration.name
    }

    override fun getUniqueId(): UUID {
        return this.configuration.uniqueId
    }

    override fun getFirstLogin(): Long {
        return this.configuration.firstLogin
    }

    override fun getLastLogin(): Long {
        return this.configuration.lastLogin
    }

    override fun getOnlineTime(): Long {
        return this.configuration.onlineTime
    }

    override fun getLastPlayerConnection(): PlayerConnection {
        return this.lastPlayerConnection
    }

    override fun isOnline(): Boolean {
        return false
    }

    override fun getDisplayName(): String {
        return this.configuration.displayName
    }

    override fun getWebConfig(): PlayerWebConfig {
        return this.configuration.webConfig
    }

    override fun getPermissions(): Collection<Permission> {
        return this.permissionPlayer.getPermissions()
    }

    override fun getHighestTopLevelPermissionGroup(): CompletableFuture<PermissionGroup> {
        return this.permissionPlayer.getHighestTopLevelPermissionGroup()
    }

    override fun getTopLevelPermissionGroups(): CompletableFuture<List<PermissionGroup>> {
        return this.permissionPlayer.getTopLevelPermissionGroups()
    }

    override fun hasTopLevelGroup(groupName: String): Boolean {
        return this.permissionPlayer.hasTopLevelGroup(groupName)
    }

    override fun hasPermission(permission: String, processGroup: String?): CompletableFuture<Boolean> {
        return this.permissionPlayer.hasPermission(permission, processGroup)
    }

    override fun toOfflinePlayer(): OfflineCloudPlayer {
        return this
    }

    override fun toConfiguration(): OfflineCloudPlayerConfiguration {
        return this.configuration
    }

    override fun createUpdateRequest(): OfflineCloudPlayerUpdateRequest {
        return OfflineCloudPlayerUpdateRequestImpl(this, this.cloudPlayerService, this.permissionFactory)
    }


}