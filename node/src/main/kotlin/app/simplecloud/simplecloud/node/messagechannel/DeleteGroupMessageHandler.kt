package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.ea.async.Async.await
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class DeleteGroupMessageHandler @Inject constructor(
    private val groupService: CloudProcessGroupService,
) : MessageHandler<String, Unit> {

    override fun handleMessage(message: String, sender: NetworkComponent): CompletableFuture<Unit> {
        val processGroup = await(this.groupService.findByName(message))
        return processGroup.createDeleteRequest().submit()
    }
}