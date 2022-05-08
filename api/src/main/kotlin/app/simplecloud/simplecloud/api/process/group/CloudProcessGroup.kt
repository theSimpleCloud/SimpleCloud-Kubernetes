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

package app.simplecloud.simplecloud.api.process.group

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupDeleteRequest
import app.simplecloud.simplecloud.api.request.group.update.CloudProcessGroupUpdateRequest
import app.simplecloud.simplecloud.api.utils.Identifiable
import app.simplecloud.simplecloud.api.utils.Nameable


interface CloudProcessGroup : Nameable, Identifiable<String> {

    fun getMaxMemory(): Int

    fun getMaxPlayers(): Int

    fun getOnlinePlayerCount(): Int

    fun isInMaintenance(): Boolean

    fun getImage(): Image

    fun getProcessGroupType(): ProcessGroupType

    fun getJoinPermission(): String?

    fun isStatic(): Boolean

    fun isStateUpdatingEnabled(): Boolean

    fun getStartPriority(): Int

    fun toConfiguration(): AbstractCloudProcessGroupConfiguration

    fun createUpdateRequest(): CloudProcessGroupUpdateRequest

    fun createDeleteRequest(): CloudProcessGroupDeleteRequest

}