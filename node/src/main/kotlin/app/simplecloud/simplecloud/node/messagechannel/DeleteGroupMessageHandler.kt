package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class DeleteGroupMessageHandler @Inject constructor(
    private val groupService: CloudProcessGroupService,
) : MessageHandler<String, Unit> {

    override fun handleMessage(message: String, sender: NetworkComponent): CompletableFuture<Unit> = CloudScope.future {
        val processGroup = groupService.findByName(message).await()
        processGroup.createDeleteRequest().submit().await()
    }
}