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

package app.simplecloud.simplecloud.api.process.onlinestrategy

import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.utils.Identifiable
import app.simplecloud.simplecloud.api.utils.Nameable

/**
 * Created by IntelliJ IDEA.
 * Date: 16.03.2021
 * Time: 19:50
 * @author Frederick Baier
 *
 * Describes how many processes shall be online
 *
 */
interface ProcessesOnlineCountStrategy : Nameable, Identifiable<String> {

    /**
     * Returns the amount of processes that should be online at the moment the method gets called
     * According to the returned value processes will be stopped or started
     */
    fun calculateOnlineCount(group: CloudProcessGroup): Int

    /**
     * Returns the configuration of this strategy
     */
    fun toConfiguration(): ProcessOnlineCountStrategyConfiguration

    override fun getIdentifier(): String {
        return getName()
    }

    interface Factory {

        fun create(configuration: ProcessOnlineCountStrategyConfiguration): ProcessesOnlineCountStrategy

    }

}