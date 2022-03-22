package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class UpdatePermissionGroupMessageHandler @Inject constructor(
    private val groupService: InternalPermissionGroupService
) : MessageHandler<PermissionGroupConfiguration, Unit> {

    override fun handleMessage(message: PermissionGroupConfiguration, sender: NetworkComponent): CompletableFuture<Unit> {
        return this.groupService.updateGroupInternal(message)
    }
}