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
 * Time: 12:56
 * @author Frederick Baier
 */
class CloudProxyProcessGroupConfiguration(
    name: String,
    maxMemory: Int,
    maxPlayers: Int,
    maintenance: Boolean,
    imageName: String?,
    static: Boolean,
    stateUpdating: Boolean,
    startPriority: Int,
    joinPermission: String?,
    val startPort: Int
) : AbstractCloudProcessGroupConfiguration(
    name,
    maxMemory,
    maxPlayers,
    maintenance,
    imageName,
    static,
    stateUpdating,
    startPriority,
    joinPermission,
    ProcessGroupType.PROXY
) {

    private constructor() : this(
        "",
        1,
        1,
        false,
        "",
        false,
        false,
        1,
        "",
        1
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as CloudProxyProcessGroupConfiguration

        if (startPort != other.startPort) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + startPort
        return result
    }


}