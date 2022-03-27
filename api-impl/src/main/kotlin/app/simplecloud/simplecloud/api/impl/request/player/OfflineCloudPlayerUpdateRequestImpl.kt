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

package app.simplecloud.simplecloud.api.impl.request.player

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.impl.request.permission.AbstractPermissionEntityUpdateRequest
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.PermissionPlayer
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.request.player.OfflineCloudPlayerUpdateRequest
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 18:05
 * @author Frederick Baier
 *
 */
open class OfflineCloudPlayerUpdateRequestImpl(
    private val offlinePlayer: OfflineCloudPlayer,
    private val internalService: InternalCloudPlayerService,
    private val permissionFactory: Permission.Factory
) : AbstractPermissionEntityUpdateRequest(offlinePlayer, permissionFactory), OfflineCloudPlayerUpdateRequest {

    @Volatile
    protected var displayName: String = this.offlinePlayer.getDisplayName()

    @Volatile
    protected var webConfig: PlayerWebConfig = this.offlinePlayer.getWebConfig()

    override fun clearPermissions(): OfflineCloudPlayerUpdateRequest {
        super.clearPermissions()
        return this
    }

    override fun addPermission(permission: Permission): OfflineCloudPlayerUpdateRequest {
        super.addPermission(permission)
        return this
    }

    override fun removePermission(permissionString: String): OfflineCloudPlayerUpdateRequest {
        super.removePermission(permissionString)
        return this
    }

    override fun clearPermissionGroups(): OfflineCloudPlayerUpdateRequest {
        super.clearPermissionGroups()
        return this
    }

    override fun addPermissionGroup(permissionGroup: PermissionGroup, expiresAt: Long): OfflineCloudPlayerUpdateRequest {
        super.addPermissionGroup(permissionGroup, expiresAt)
        return this
    }

    override fun removePermissionGroup(groupName: String): OfflineCloudPlayerUpdateRequest {
        super.removePermissionGroup(groupName)
        return this
    }

    override fun getEntity(): PermissionPlayer {
        return this.offlinePlayer
    }

    override fun setDisplayName(name: String): OfflineCloudPlayerUpdateRequest {
        this.displayName = name
        return this
    }

    override fun setWebConfig(webConfig: PlayerWebConfig): OfflineCloudPlayerUpdateRequest {
        this.webConfig = webConfig
        return this
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        val lastPlayerConnection = offlinePlayer.getLastPlayerConnection()
        val connectionConfiguration = PlayerConnectionConfiguration(
            lastPlayerConnection.getUniqueId(),
            lastPlayerConnection.getVersion(),
            lastPlayerConnection.getName(),
            lastPlayerConnection.getAddress(),
            lastPlayerConnection.isOnlineMode()
        )
        val permissionPlayerConfiguration = PermissionPlayerConfiguration(
            offlinePlayer.getUniqueId(),
            permissions.map { it.toConfiguration() }
        )
        val offlineCloudPlayerConfiguration = OfflineCloudPlayerConfiguration(
            offlinePlayer.getName(),
            offlinePlayer.getUniqueId(),
            offlinePlayer.getFirstLogin(),
            offlinePlayer.getLastLogin(),
            offlinePlayer.getOnlineTime(),
            displayName,
            connectionConfiguration,
            webConfig,
            permissionPlayerConfiguration
        )
        return@future internalService.updateOfflinePlayerInternal(offlineCloudPlayerConfiguration)
    }
}