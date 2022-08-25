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

package app.simplecloud.simplecloud.api.impl.template

import app.simplecloud.simplecloud.api.image.Image
import app.simplecloud.simplecloud.api.impl.image.ImageImpl
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.template.ProcessTemplateType
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration

/**
 * Date: 17.08.22
 * Time: 14:50
 * @author Frederick Baier
 *
 */
abstract class AbstractProcessTemplate(
    private val configuration: AbstractProcessTemplateConfiguration,
) : ProcessTemplate {

    override fun getMaxMemory(): Int {
        return this.configuration.maxMemory
    }

    override fun getMaxPlayers(): Int {
        return this.configuration.maxPlayers
    }

    override fun isInMaintenance(): Boolean {
        return this.configuration.maintenance
    }

    override fun getImage(): Image {
        val imageName = this.configuration.imageName
            ?: throw app.simplecloud.simplecloud.api.impl.exception.NoImageProvidedException(getName())
        return ImageImpl(imageName)
    }

    override fun getProcessTemplateType(): ProcessTemplateType {
        return this.configuration.type
    }

    override fun getJoinPermission(): String? {
        return this.configuration.joinPermission
    }

    override fun isStateUpdatingEnabled(): Boolean {
        return this.configuration.stateUpdating
    }

    override fun getStartPriority(): Int {
        return this.configuration.startPriority
    }

    override fun isActive(): Boolean {
        return this.configuration.active
    }

    override fun toConfiguration(): AbstractProcessTemplateConfiguration {
        return this.configuration
    }

    override fun getName(): String {
        return this.configuration.name
    }
}