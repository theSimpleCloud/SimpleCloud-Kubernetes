package app.simplecloud.simplecloud.api.internal.request.player

import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.request.player.CloudPlayerUpdateRequest

/**
 * Date: 13.01.22
 * Time: 18:52
 * @author Frederick Baier
 *
 */
interface InternalCloudPlayerUpdateRequest : CloudPlayerUpdateRequest {

    override fun setDisplayName(name: String): InternalCloudPlayerUpdateRequest

    override fun setWebConfig(webConfig: PlayerWebConfig): InternalCloudPlayerUpdateRequest

    fun setConnectedProxyName(name: String): InternalCloudPlayerUpdateRequest

    fun setConnectedServerName(name: String): InternalCloudPlayerUpdateRequest

}