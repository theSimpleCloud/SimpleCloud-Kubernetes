package app.simplecloud.simplecloud.plugin.proxy.request.login

import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.plugin.OnlineCountProvider

/**
 * Date: 18.01.22
 * Time: 10:12
 * @author Frederick Baier
 *
 */
class PlayerPostLoginRequestHandler(
    private val request: PlayerConnectionConfiguration,
    private val selfProcess: CloudProcess,
    private val onlineCountProvider: OnlineCountProvider
) {

    fun handle() {
        updateOnlineCount()
    }

    private fun updateOnlineCount() {
        val updateRequest = this.selfProcess.createUpdateRequest()
        updateRequest as InternalProcessUpdateRequest
        updateRequest.setOnlinePlayers(this.onlineCountProvider.getOnlineCount())
        updateRequest.submit()
    }


}