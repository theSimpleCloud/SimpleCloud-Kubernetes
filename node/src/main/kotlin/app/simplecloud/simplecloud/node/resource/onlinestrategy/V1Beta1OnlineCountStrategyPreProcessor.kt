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

package app.simplecloud.simplecloud.node.resource.onlinestrategy

import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPreProcessor
import app.simplecloud.simplecloud.node.repository.distributed.DistributedOnlineCountStrategyRepository

/**
 * Date: 07.03.23
 * Time: 13:45
 * @author Frederick Baier
 *
 */
class V1Beta1OnlineCountStrategyPreProcessor(
    private val distributedStrategyRepository: DistributedOnlineCountStrategyRepository,
) : ResourceVersionRequestPreProcessor<V1Beta1ProcessOnlineCountStrategySpec>() {

    override fun processCreate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1ProcessOnlineCountStrategySpec,
    ): RequestPreProcessorResult<V1Beta1ProcessOnlineCountStrategySpec> {
        this.distributedStrategyRepository.save(
            name,
            convertSpecToConfig(name, spec)
        )
        return RequestPreProcessorResult.continueNormally()
    }

    override fun processUpdate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1ProcessOnlineCountStrategySpec,
    ): RequestPreProcessorResult<V1Beta1ProcessOnlineCountStrategySpec> {
        this.distributedStrategyRepository.save(name, convertSpecToConfig(name, spec))
        return RequestPreProcessorResult.continueNormally()
    }

    override fun processDelete(
        group: String,
        version: String,
        kind: String,
        name: String,
    ): RequestPreProcessorResult<Any> {
        this.distributedStrategyRepository.remove(name)
        return RequestPreProcessorResult.continueNormally()
    }

    private fun convertSpecToConfig(
        name: String,
        spec: V1Beta1ProcessOnlineCountStrategySpec,
    ): ProcessOnlineCountStrategyConfiguration {
        return ProcessOnlineCountStrategyConfiguration(
            name,
            spec.className,
            spec.targetGroupNames.toSet(),
            createMap(spec.dataKeys, spec.dataValues)
        )
    }

    private fun createMap(keys: Array<String>, values: Array<String>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (index in keys.indices) {
            map[keys[0]] = values[0]
        }
        return map
    }

}