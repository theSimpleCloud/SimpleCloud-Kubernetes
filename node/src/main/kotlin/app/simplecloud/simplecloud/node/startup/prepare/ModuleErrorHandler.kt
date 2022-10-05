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

package app.simplecloud.simplecloud.node.startup.prepare

import org.apache.logging.log4j.LogManager

/**
 * Date: 05.10.22
 * Time: 10:14
 * @author Frederick Baier
 *
 */
class ModuleErrorHandler : (Throwable) -> Unit {

    override fun invoke(throwable: Throwable) {
        logger.error("Caught module error:", throwable)
    }

    companion object {
        private val logger = LogManager.getLogger(ModuleErrorHandler::class.java)
    }

}