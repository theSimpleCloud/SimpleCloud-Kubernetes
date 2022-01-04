package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.node.messagechannel.DeleteGroupMessageHandler
import app.simplecloud.simplecloud.node.messagechannel.StartProcessMessageHandler
import app.simplecloud.simplecloud.node.messagechannel.UpdateGroupMessageHandler
import com.google.inject.Inject
import com.google.inject.Injector
import java.util.concurrent.CompletableFuture

class InitMessageChannelsTask @Inject constructor(
    private val messageChannelManager: MessageChannelManager,
    private val injector: Injector
) {

    fun run(): CompletableFuture<Unit> {
        registerGroupDeleteMessageChannel()
        registerGroupUpdateMessageChannel()
        registerStartProcessMessageChannel()
        return unitFuture()
    }

    private fun registerStartProcessMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<ProcessStartConfiguration, CloudProcessConfiguration>("internal_start_process")
        val startProcessMessageHandler = this.injector.getInstance(StartProcessMessageHandler::class.java)
        messageChannel.setMessageHandler(startProcessMessageHandler)
    }

    private fun registerGroupUpdateMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<AbstractCloudProcessGroupConfiguration, Unit>("internal_update_group")
        val updateGroupMessageHandler = this.injector.getInstance(UpdateGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(updateGroupMessageHandler)
    }

    private fun registerGroupDeleteMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<String, Unit>("internal_delete_group")
        val deleteGroupMessageHandler = this.injector.getInstance(DeleteGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(deleteGroupMessageHandler)
    }

}