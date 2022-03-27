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
import app.simplecloud.simplecloud.api.internal.request.player.InternalCloudPlayerUpdateRequest
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import java.util.concurrent.CompletableFuture

/**
 * Date: 13.01.22
 * Time: 18:53
 * @author Frederick Baier
 *
 */
class CloudPlayerUpdateRequestImpl(
    private val cloudPlayer: CloudPlayer,
    private val internalService: InternalCloudPlayerService,
    private val permissionFactory: Permission.Factory
) : OfflineCloudPlayerUpdateRequestImpl(cloudPlayer, internalService, permissionFactory),
    InternalCloudPlayerUpdateRequest {

    @Volatile
    private var connectedProxyName: String = this.cloudPlayer.getCurrentProxyName()

    @Volatile
    private var connectedServerName: String? = this.cloudPlayer.getCurrentServerName()

    override fun clearPermissions(): InternalCloudPlayerUpdateRequest {
        super.clearPermissions()
        return this
    }

    override fun addPermission(permission: Permission): InternalCloudPlayerUpdateRequest {
        super.addPermission(permission)
        return this
    }

    override fun removePermission(permissionString: String): InternalCloudPlayerUpdateRequest {
        super.removePermission(permissionString)
        return this
    }

    override fun clearPermissionGroups(): InternalCloudPlayerUpdateRequest {
        super.clearPermissionGroups()
        return this
    }

    override fun addPermissionGroup(
        permissionGroup: PermissionGroup,
        expiresAt: Long
    ): InternalCloudPlayerUpdateRequest {
        super.addPermissionGroup(permissionGroup, expiresAt)
        return this
    }

    override fun removePermissionGroup(groupName: String): InternalCloudPlayerUpdateRequest {
        super.removePermissionGroup(groupName)
        return this
    }

    override fun setDisplayName(name: String): InternalCloudPlayerUpdateRequest {
        super.setDisplayName(name)
        return this
    }

    override fun setConnectedProxyName(name: String): InternalCloudPlayerUpdateRequest {
        this.connectedProxyName = name
        return this
    }

    override fun setConnectedServerName(name: String): InternalCloudPlayerUpdateRequest {
        this.connectedServerName = name
        return this
    }

    override fun setWebConfig(webConfig: PlayerWebConfig): InternalCloudPlayerUpdateRequest {
        super.setWebConfig(webConfig)
        return this
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        val playerConnection = cloudPlayer.getPlayerConnection()
        val connectionConfiguration = PlayerConnectionConfiguration(
            playerConnection.getUniqueId(),
            playerConnection.getVersion(),
            playerConnection.getName(),
            playerConnection.getAddress(),
            playerConnection.isOnlineMode()
        )
        val permissionPlayerConfiguration = PermissionPlayerConfiguration(
            cloudPlayer.getUniqueId(),
            permissions.map { it.toConfiguration() }
        )
        val cloudPlayerConfiguration = CloudPlayerConfiguration(
            cloudPlayer.getName(),
            cloudPlayer.getUniqueId(),
            cloudPlayer.getFirstLogin(),
            cloudPlayer.getLastLogin(),
            cloudPlayer.getOnlineTime(),
            connectionConfiguration,
            displayName,
            webConfig,
            permissionPlayerConfiguration,
            connectedServerName,
            connectedProxyName
        )
        return@future internalService.updateOnlinePlayerInternal(cloudPlayerConfiguration)
    }
}