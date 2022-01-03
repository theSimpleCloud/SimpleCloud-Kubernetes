package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.handler.ProcessGroupUpdateHandler
import com.ea.async.Async.await
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class UpdateGroupMessageHandler @Inject constructor(
    private val groupUpdateHandler: ProcessGroupUpdateHandler
) : MessageHandler<AbstractCloudProcessGroupConfiguration, Unit> {

    override fun handleMessage(message: AbstractCloudProcessGroupConfiguration, sender: NetworkComponent): CompletableFuture<Unit> {
        await(this.groupUpdateHandler.update(message))
        return unitFuture()
    }
}