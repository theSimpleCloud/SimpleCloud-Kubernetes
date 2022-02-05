package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class StartProcessMessageHandler @Inject constructor(
    private val processService: InternalCloudProcessService
) : MessageHandler<ProcessStartConfiguration, CloudProcessConfiguration> {

    override fun handleMessage(message: ProcessStartConfiguration, sender: NetworkComponent): CompletableFuture<CloudProcessConfiguration> {
        return this.processService.startNewProcessInternal(message).thenApply { it.toConfiguration() }
    }
}