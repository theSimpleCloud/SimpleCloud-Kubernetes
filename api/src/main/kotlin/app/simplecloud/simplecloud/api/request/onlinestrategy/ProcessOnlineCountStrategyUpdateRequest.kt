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

package app.simplecloud.simplecloud.api.request.onlinestrategy

import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.utils.Request

/**
 * Created by IntelliJ IDEA.
 * Date: 01/07/2021
 * Time: 21:34
 * @author Frederick Baier
 */
interface ProcessOnlineCountStrategyUpdateRequest : Request<Unit> {

    /**
     * Returns the strategy to update
     */
    fun getStrategy(): ProcessesOnlineCountStrategy

    /**
     * Clears the groups this strategy was linked to
     * @return this
     */
    fun clearTargetGroups(): ProcessOnlineCountStrategyUpdateRequest

    /**
     * Links a group to this strategy.
     * The given group now uses this strategy if no other strategy points to the specified group.
     * @return this
     */
    fun addTargetGroup(name: String): ProcessOnlineCountStrategyUpdateRequest

    /**
     *  Removes a group from the strategy
     * @return this
     */
    fun removeTargetGroup(name: String): ProcessOnlineCountStrategyUpdateRequest

    /**
     * Sets the data this strategy needs
     * @return this
     */
    fun setData(data: Map<String, String>): ProcessOnlineCountStrategyUpdateRequest

}