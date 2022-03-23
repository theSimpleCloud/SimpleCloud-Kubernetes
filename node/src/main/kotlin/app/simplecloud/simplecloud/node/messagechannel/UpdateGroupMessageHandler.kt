package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class UpdateGroupMessageHandler @Inject constructor(
    private val groupService: InternalCloudProcessGroupService
) : MessageHandler<AbstractCloudProcessGroupConfiguration, Unit> {

    override fun handleMessage(
        message: AbstractCloudProcessGroupConfiguration,
        sender: NetworkComponent
    ): CompletableFuture<Unit> = CloudScope.future {
        groupService.updateGroupInternal(message)
    }
}