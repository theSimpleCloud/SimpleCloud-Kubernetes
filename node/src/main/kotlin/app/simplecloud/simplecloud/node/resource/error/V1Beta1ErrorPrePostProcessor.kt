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

package app.simplecloud.simplecloud.node.resource.error

import app.simplecloud.simplecloud.module.api.error.configuration.ErrorConfiguration
import app.simplecloud.simplecloud.module.api.impl.repository.distributed.DistributedErrorRepository
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPrePostProcessor
import java.util.*

/**
 * Date: 18.03.23
 * Time: 13:37
 * @author Frederick Baier
 *
 */
class V1Beta1ErrorPrePostProcessor(
    private val repository: DistributedErrorRepository,
) : ResourceVersionRequestPrePostProcessor<V1Beta1ErrorResourceSpec>() {

    override fun preCreate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1ErrorResourceSpec,
    ): RequestPreProcessorResult<V1Beta1ErrorResourceSpec> {
        checkConstraint(isUUID(name), "Name must be a uuid")
        return RequestPreProcessorResult.continueNormally()
    }

    override fun postCreate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1ErrorResourceSpec,
    ) {
        val uuid = UUID.fromString(name)
        repository.save(
            uuid,
            ErrorConfiguration(
                uuid,
                spec.errorType,
                spec.shortMessage,
                spec.message,
                spec.processName,
                spec.timeStamp,
                createMap(spec.dataKeys, spec.dataValues)
            )
        )
    }

    private fun isUUID(string: String): Boolean {
        return try {
            UUID.fromString(string)
            true
        } catch (ex: Exception) {
            false
        }
    }

    override fun preDelete(
        group: String,
        version: String,
        kind: String,
        name: String,
    ): RequestPreProcessorResult<Any> {
        checkConstraint(isUUID(name), "Name must be a uuid")
        return RequestPreProcessorResult.continueNormally()
    }

    override fun postDelete(
        group: String,
        version: String,
        kind: String,
        name: String,
        deletedSpec: V1Beta1ErrorResourceSpec,
    ) {
        val uuid = UUID.fromString(name)
        repository.remove(uuid)
    }

    private fun createMap(keys: Array<String>, values: Array<String>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (index in keys.indices) {
            map[keys[0]] = values[0]
        }
        return map
    }

}