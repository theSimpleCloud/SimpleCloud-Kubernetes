package app.simplecloud.simplecloud.api.impl.permission

import app.simplecloud.simplecloud.api.impl.permission.group.PermissionGroupImpl
import app.simplecloud.simplecloud.api.internal.service.InternalPermissionGroupService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.repository.PermissionGroupRepository
import app.simplecloud.simplecloud.api.service.PermissionGroupService
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder

/**
 * Date: 23.03.22
 * Time: 08:56
 * @author Frederick Baier
 *
 */
class BinderModule : AbstractModule() {

    override fun configure() {

        bind(PermissionGroupService::class.java).to(PermissionGroupServiceImpl::class.java)
        bind(InternalPermissionGroupService::class.java).to(PermissionGroupServiceImpl::class.java)

        bind(PermissionGroupRepository::class.java).to(PermissionGroupRepositoryImpl::class.java)

        install(
            FactoryModuleBuilder()
                .implement(Permission::class.java, PermissionImpl::class.java)
                .build(Permission.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(PermissionGroup::class.java, PermissionGroupImpl::class.java)
                .build(PermissionGroup.Factory::class.java)
        )
    }

}