package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.permission.service.PermissionGroupService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.ea.async.Async.await
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class DeletePermissionGroupMessageHandler @Inject constructor(
    private val groupService: PermissionGroupService,
) : MessageHandler<String, Unit> {

    override fun handleMessage(message: String, sender: NetworkComponent): CompletableFuture<Unit> {
        val permissionGroup = await(this.groupService.findPermissionGroupByName(message))
        return this.groupService.createDeleteRequest(permissionGroup).submit()
    }
}