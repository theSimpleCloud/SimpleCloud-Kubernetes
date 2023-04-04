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

package app.simplecloud.simplecloud.node.resource.group

import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.MinValue
import app.simplecloud.simplecloud.api.resourcedefinition.limitation.annotation.Optional

/**
 * Date: 08.03.23
 * Time: 18:23
 * @author Frederick Baier
 *
 */
data class V1Beta1ProxyGroupSpec(
    @MinValue(128)
    val maxMemory: Int,
    @MinValue(-1)
    val maxPlayers: Int,
    val maintenance: Boolean,
    @Optional
    val imageName: String?,
    val stateUpdating: Boolean,
    val startPriority: Int,
    @Optional
    val joinPermission: String?,
    val active: Boolean,
)