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

package app.simplecloud.simplecloud.node.repository.mongo.onlinecountstrategy

import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id

/**
 * Date: 25.03.22
 * Time: 09:28
 * @author Frederick Baier
 *
 */
@Entity("online_count_strategies")
class OnlineCountStrategyEntity(
    @Id
    val name: String,
    val className: String,
    val targetGroupNames: Set<String>,
    val data: Map<String, String>
) {

    private constructor() : this("", "", emptySet(), emptyMap())

    fun toConfiguration(): ProcessOnlineCountStrategyConfiguration {
        return ProcessOnlineCountStrategyConfiguration(
            this.name,
            this.className,
            this.targetGroupNames,
            this.data
        )
    }

    companion object {

        fun fromConfiguration(configuration: ProcessOnlineCountStrategyConfiguration): OnlineCountStrategyEntity {
            return OnlineCountStrategyEntity(
                configuration.name,
                configuration.className,
                configuration.targetGroupNames,
                configuration.data
            )
        }

    }

}