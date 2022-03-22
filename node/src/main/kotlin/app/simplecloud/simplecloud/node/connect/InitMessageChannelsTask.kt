package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.node.messagechannel.*
import com.google.inject.Inject
import com.google.inject.Injector
import java.util.concurrent.CompletableFuture

class InitMessageChannelsTask @Inject constructor(
    private val messageChannelManager: MessageChannelManager,
    private val injector: Injector
) {

    fun run(): CompletableFuture<Unit> {
        registerPermissionGroupDeleteMessageChannel()
        registerPermissionGroupUpdateMessageChannel()
        registerGroupDeleteMessageChannel()
        registerGroupUpdateMessageChannel()
        registerStartProcessMessageChannel()
        registerPlayerLoginMessageChannel()
        return unitFuture()
    }

    private fun registerPlayerLoginMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<PlayerConnectionConfiguration, CloudPlayerConfiguration>("internal_player_login")
        val messageHandler = this.injector.getInstance(CloudPlayerLoginMessageHandler::class.java)
        messageChannel.setMessageHandler(messageHandler)
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

    private fun registerPermissionGroupUpdateMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<PermissionGroupConfiguration, Unit>("internal_update_permission_group")
        val updateGroupMessageHandler = this.injector.getInstance(UpdatePermissionGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(updateGroupMessageHandler)
    }

    private fun registerPermissionGroupDeleteMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<String, Unit>("internal_delete_permission_group")
        val deleteGroupMessageHandler = this.injector.getInstance(DeletePermissionGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(deleteGroupMessageHandler)
    }

}