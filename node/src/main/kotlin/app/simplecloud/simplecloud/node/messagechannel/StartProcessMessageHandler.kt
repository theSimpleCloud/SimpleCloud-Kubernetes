package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.handler.ProcessGroupUpdateHandler
import com.ea.async.Async.await
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class StartProcessMessageHandler @Inject constructor(
    private val processService: InternalCloudProcessService
) : MessageHandler<ProcessStartConfiguration, CloudProcessConfiguration> {

    override fun handleMessage(message: ProcessStartConfiguration, sender: NetworkComponent): CompletableFuture<CloudProcessConfiguration> {
        return this.processService.startNewProcessInternal(message).thenApply { it.toConfiguration() }
    }
}