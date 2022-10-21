package app.simplecloud.simplecloud.plugin.proxy.messagechannel

import app.simplecloud.simplecloud.api.internal.InternalCloudAPI
import app.simplecloud.simplecloud.plugin.proxy.CloudPlayerProxyActions

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 20.10.22
 * Time: 21:16
 */
class MessageChannelsInitializer(
    private val cloudAPI: InternalCloudAPI,
    private val cloudPlayerProxyActions: CloudPlayerProxyActions
) {

    private val internalMessageChannelProvider = cloudAPI.getInternalMessageChannelProvider()

    fun initializeMessageChannels() {
        registerCloudPlayerMessageChannel()
        registerCloudPlayerActionBarChannel()
    }

    private fun registerCloudPlayerMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalCloudPlayerMessageChannel()
        val messageHandler = CloudPlayerMessageMessageHandler(this.cloudPlayerProxyActions)
        messageChannel.setMessageHandler(messageHandler)
    }

    private fun registerCloudPlayerActionBarChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalCloudPlayerActionBarChannel()
        val messageHandler = CloudPlayerActionBarMessageHandler(this.cloudPlayerProxyActions)
        messageChannel.setMessageHandler(messageHandler)
    }

}