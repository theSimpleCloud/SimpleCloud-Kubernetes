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

package app.simplecloud.simplecloud.node.resource.permissiongroup

import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedPermissionGroupRepository
import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPreProcessor

/**
 * Date: 07.03.23
 * Time: 13:45
 * @author Frederick Baier
 *
 */
class V1Beta1PermissionGroupPreProcessor(
    private val distributedGroupRepository: DistributedPermissionGroupRepository,
) : ResourceVersionRequestPreProcessor<V1Beta1PermissionGroupSpec>() {

    override fun processCreate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1PermissionGroupSpec,
    ): RequestPreProcessorResult<V1Beta1PermissionGroupSpec> {
        this.distributedGroupRepository.save(
            name,
            convertSpecToConfig(name, spec)
        ).join()
        return RequestPreProcessorResult.continueNormally()
    }

    override fun processUpdate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1PermissionGroupSpec,
    ): RequestPreProcessorResult<V1Beta1PermissionGroupSpec> {
        this.distributedGroupRepository.save(name, convertSpecToConfig(name, spec)).join()
        return RequestPreProcessorResult.continueNormally()
    }

    override fun processDelete(
        group: String,
        version: String,
        kind: String,
        name: String,
    ): RequestPreProcessorResult<Any> {
        this.distributedGroupRepository.remove(name).join()
        return RequestPreProcessorResult.continueNormally()
    }

    private fun convertSpecToConfig(
        name: String,
        spec: V1Beta1PermissionGroupSpec,
    ): PermissionGroupConfiguration {
        return PermissionGroupConfiguration(
            name,
            spec.priority,
            spec.permissions.map {
                PermissionConfiguration(
                    it.permissionString,
                    it.active,
                    it.expiresAtTimestamp,
                    it.targetProcessGroup
                )
            }
        )
    }

}