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