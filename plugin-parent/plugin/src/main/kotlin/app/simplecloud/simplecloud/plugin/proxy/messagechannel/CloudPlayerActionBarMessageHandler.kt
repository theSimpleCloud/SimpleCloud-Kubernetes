package app.simplecloud.simplecloud.plugin.proxy.messagechannel

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.player.message.ActionBarConfiguration
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import app.simplecloud.simplecloud.plugin.proxy.CloudPlayerProxyActions
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 20.10.22
 * Time: 21:18
 */
class CloudPlayerActionBarMessageHandler(
    private val cloudPlayerProxyActions: CloudPlayerProxyActions
) : MessageHandler<ActionBarConfiguration, Unit> {

    override fun handleMessage(message: ActionBarConfiguration, sender: NetworkComponent): CompletableFuture<Unit> {
        this.cloudPlayerProxyActions.sendActionBar(message)
        return unitFuture()
    }

}