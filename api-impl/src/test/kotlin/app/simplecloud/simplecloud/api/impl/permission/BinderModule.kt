/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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