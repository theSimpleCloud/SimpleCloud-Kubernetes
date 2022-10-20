package app.simplecloud.simplecloud.plugin.proxy.messagechannel

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.player.message.MessageConfiguration
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import app.simplecloud.simplecloud.plugin.proxy.CloudPlayerProxyActions
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 20.10.22
 * Time: 21:18
 */
class CloudPlayerMessageMessageHandler(
    private val cloudPlayerProxyActions: CloudPlayerProxyActions
) : MessageHandler<MessageConfiguration, Unit> {

    override fun handleMessage(message: MessageConfiguration, sender: NetworkComponent): CompletableFuture<Unit> {
        this.cloudPlayerProxyActions.sendMessage(message)
        return unitFuture()
    }

}