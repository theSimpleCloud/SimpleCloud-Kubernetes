package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgnitePermissionGroupRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.node.mongo.permission.MongoPermissionGroupRepository
import app.simplecloud.simplecloud.node.mongo.permission.PermissionGroupEntity
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
    private val mongoRepository: MongoPermissionGroupRepository,
    private val igniteRepository: IgnitePermissionGroupRepository,
    private val groupFactory: PermissionGroup.Factory,
    private val permissionFactory: Permission.Factory,
) : AbstractPermissionGroupService(igniteRepository, groupFactory, permissionFactory) {

    override fun updateGroupInternal(configuration: PermissionGroupConfiguration): CompletableFuture<Unit> {
        this.igniteRepository.save(configuration.name, configuration)
        saveToDatabase(configuration)
        return unitFuture()
    }

    private fun saveToDatabase(configuration: PermissionGroupConfiguration) {
        val groupEntity = PermissionGroupEntity.fromConfiguration(configuration)
        this.mongoRepository.save(groupEntity.name, groupEntity)
    }

    override fun deleteGroupInternal(group: PermissionGroup) {
        this.igniteRepository.remove(group.getName())
        deleteGroupFromDatabase(group)
    }

    private fun deleteGroupFromDatabase(group: PermissionGroup) {
        this.mongoRepository.remove(group.getName())
    }

}