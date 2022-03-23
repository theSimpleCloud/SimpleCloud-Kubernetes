package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.permission.service.PermissionGroupService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class DeletePermissionGroupMessageHandler @Inject constructor(
    private val groupService: PermissionGroupService,
) : MessageHandler<String, Unit> {

    override fun handleMessage(
        message: String,
        sender: NetworkComponent
    ): CompletableFuture<Unit> = CloudScope.future {
        val permissionGroup = groupService.findPermissionGroupByName(message).await()
        groupService.createDeleteRequest(permissionGroup).submit().await()
    }
}