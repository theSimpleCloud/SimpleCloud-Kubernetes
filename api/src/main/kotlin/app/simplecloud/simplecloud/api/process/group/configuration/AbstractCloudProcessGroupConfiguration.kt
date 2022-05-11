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

package app.simplecloud.simplecloud.api.process.group.configuration

import app.simplecloud.simplecloud.api.process.group.ProcessGroupType

/**
 * Created by IntelliJ IDEA.
 * Date: 01/07/2021
 * Time: 12:57
 * @author Frederick Baier
 */
abstract class AbstractCloudProcessGroupConfiguration(
    val name: String,
    val maxMemory: Int,
    val maxPlayers: Int,
    val maintenance: Boolean,
    val imageName: String?,
    val static: Boolean,
    val stateUpdating: Boolean,
    val startPriority: Int,
    val joinPermission: String?,
    val type: ProcessGroupType
) : java.io.Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractCloudProcessGroupConfiguration

        if (name != other.name) return false
        if (maxMemory != other.maxMemory) return false
        if (maxPlayers != other.maxPlayers) return false
        if (maintenance != other.maintenance) return false
        if (imageName != other.imageName) return false
        if (static != other.static) return false
        if (stateUpdating != other.stateUpdating) return false
        if (startPriority != other.startPriority) return false
        if (joinPermission != other.joinPermission) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + maxMemory
        result = 31 * result + maxPlayers
        result = 31 * result + maintenance.hashCode()
        result = 31 * result + (imageName?.hashCode() ?: 0)
        result = 31 * result + static.hashCode()
        result = 31 * result + stateUpdating.hashCode()
        result = 31 * result + startPriority
        result = 31 * result + (joinPermission?.hashCode() ?: 0)
        result = 31 * result + type.hashCode()
        return result
    }
}