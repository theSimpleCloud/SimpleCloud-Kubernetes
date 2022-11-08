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

package app.simplecloud.simplecloud.module.api.error

import app.simplecloud.simplecloud.module.api.error.configuration.ErrorConfiguration


/**
 * Date: 10.10.22
 * Time: 12:15
 * @author Frederick Baier
 *
 */
interface Error {

    /**
     * Returns a short description of the error
     */
    fun getShortMessage(): String

    /**
     * Returns the long description of the error
     */
    fun getMessage(): String

    /**
     * Returns the process name the error is associated with
     */
    fun getProcessName(): String

    /**
     * Returns whether this error can be resolved
     */
    fun isResolvable(): Boolean {
        return getResolveFunction() != null
    }

    /**
     * Returns the function that indicates whether the error was resolved
     */
    fun getResolveFunction(): ResolveFunction?

    /**
     * Returns the configuration of this error
     */
    fun toConfiguration(): ErrorConfiguration

}