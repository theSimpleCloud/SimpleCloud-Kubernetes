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

package app.simplecloud.simplecloud.module.api.impl.error

import app.simplecloud.simplecloud.module.api.error.Error
import app.simplecloud.simplecloud.module.api.error.ResolveFunction
import app.simplecloud.simplecloud.module.api.error.configuration.ErrorConfiguration

/**
 * Date: 18.10.22
 * Time: 12:45
 * @author Frederick Baier
 *
 */
class ErrorImpl(
    private val configuration: ErrorConfiguration,
) : Error {

    override fun getShortMessage(): String {
        return this.configuration.shortMessage
    }

    override fun getMessage(): String {
        return this.configuration.message
    }

    override fun getProcessName(): String {
        return this.configuration.processName
    }

    override fun getResolveFunction(): ResolveFunction? {
        return this.configuration.resolveFunction
    }

    override fun toConfiguration(): ErrorConfiguration {
        return this.configuration
    }
}