package app.simplecloud.simplecloud.plugin.startup.service

import app.simplecloud.simplecloud.api.impl.repository.ignite.IgnitePermissionGroupRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractPermissionGroupService
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.service.NodeService
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.concurrent.CompletableFuture

/**
 * Date: 20.03.22
 * Time: 13:39
 * @author Frederick Baier
 *
 */
@Singleton
class PermissionGroupServiceImpl @Inject constructor(
    private val igniteRepository: IgnitePermissionGroupRepository,
    private val groupFactory: PermissionGroup.Factory,
    private val permissionFactory: Permission.Factory,
    private val messageChannelManager: MessageChannelManager,
    private val nodeService: NodeService
) : AbstractPermissionGroupService(igniteRepository, groupFactory, permissionFactory) {


    private val deleteMessageChannel =
        this.messageChannelManager.getOrCreateMessageChannel<String, Unit>("internal_delete_permission_group")

    private val updateMessageChannel =
        this.messageChannelManager.getOrCreateMessageChannel<PermissionGroupConfiguration, Unit>("internal_update_permission_group")

    override fun updateGroupInternal(configuration: PermissionGroupConfiguration): CompletableFuture<Unit> {
        return this.nodeService.findFirst().thenApply {
            sendUpdateRequestToNode(configuration, it)
        }
    }

    override fun deleteGroupInternal(group: PermissionGroup) {
        this.nodeService.findFirst().thenApply {
            sendDeleteRequestToNode(group, it)
        }
    }

    private fun sendUpdateRequestToNode(configuration: PermissionGroupConfiguration, node: Node) {
        this.updateMessageChannel.createMessageRequest(configuration, node).submit()
    }

    private fun sendDeleteRequestToNode(group: PermissionGroup, node: Node) {
        this.deleteMessageChannel.createMessageRequest(group.getName(), node).submit()
    }

}