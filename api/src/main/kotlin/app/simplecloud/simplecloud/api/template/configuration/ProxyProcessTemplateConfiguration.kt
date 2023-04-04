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

package app.simplecloud.simplecloud.api.template.configuration

import app.simplecloud.simplecloud.api.template.ProcessTemplateType

/**
 * Created by IntelliJ IDEA.
 * Date: 01/07/2021
 * Time: 12:56
 * @author Frederick Baier
 */
class ProxyProcessTemplateConfiguration(
    name: String,
    maxMemory: Int,
    maxPlayers: Int,
    maintenance: Boolean,
    imageName: String?,
    stateUpdating: Boolean,
    startPriority: Int,
    joinPermission: String?,
    active: Boolean,
) : AbstractProcessTemplateConfiguration(
    name,
    maxMemory,
    maxPlayers,
    maintenance,
    imageName,
    stateUpdating,
    startPriority,
    joinPermission,
    active,
    ProcessTemplateType.PROXY
) {

    private constructor() : this(
        "",
        1,
        1,
        false,
        "",
        false,
        1,
        "",
        true
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        return true
    }

}