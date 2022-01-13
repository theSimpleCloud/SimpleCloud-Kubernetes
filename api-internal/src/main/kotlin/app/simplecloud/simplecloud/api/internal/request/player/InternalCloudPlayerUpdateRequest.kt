package app.simplecloud.simplecloud.api.internal.request.player

import app.simplecloud.simplecloud.api.request.player.CloudPlayerUpdateRequest

/**
 * Date: 13.01.22
 * Time: 18:52
 * @author Frederick Baier
 *
 */
interface InternalCloudPlayerUpdateRequest : CloudPlayerUpdateRequest {

    fun setConnectedProxyName(name: String): InternalCloudPlayerUpdateRequest

    fun setConnectedServerName(name: String): InternalCloudPlayerUpdateRequest

}