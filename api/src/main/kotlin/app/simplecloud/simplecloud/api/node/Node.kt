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

package app.simplecloud.simplecloud.api.node

import app.simplecloud.simplecloud.api.node.configuration.NodeConfiguration
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.api.utils.Identifiable
import app.simplecloud.simplecloud.api.utils.NetworkComponent

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 08:20
 * @author Frederick Baier
 *
 * Represents a node currently connected to the cluster
 * A node will not be available as an object if it is not connected
 *
 */
interface Node : NetworkComponent, Identifiable<String> {

    /**
     * Returns the address of the node
     */
    fun getAddress(): Address

    /**
     * Returns the configuration of this node
     */
    fun toConfiguration(): NodeConfiguration

}