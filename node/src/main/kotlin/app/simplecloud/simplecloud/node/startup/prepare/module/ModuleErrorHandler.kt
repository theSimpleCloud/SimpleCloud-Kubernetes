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

package app.simplecloud.simplecloud.node.startup.prepare.module

import app.simplecloud.simplecloud.api.error.configuration.ErrorCreateConfiguration
import app.simplecloud.simplecloud.module.api.impl.ModuleException
import org.apache.logging.log4j.LogManager
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Date: 05.10.22
 * Time: 10:14
 * @author Frederick Baier
 *
 */
class ModuleErrorHandler(
    private val errorCreateHandler: ErrorCreateHandler,
) : (Throwable) -> Unit {

    override fun invoke(throwable: Throwable) {
        val exception = ModuleException("Caught module error", throwable)
        logger.error("Caught module error:", exception)

        createErrorFromThrowable(throwable)
    }

    private fun createErrorFromThrowable(throwable: Throwable) {
        val stackTraceString = getStackTraceStringFromException(throwable)
        this.errorCreateHandler.create(
            ErrorCreateConfiguration(
                throwable.message ?: "",
                stackTraceString,
                "Cloud",
                null
            )
        )
    }

    private fun getStackTraceStringFromException(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        return sw.toString()
    }

    companion object {
        private val logger = LogManager.getLogger(ModuleErrorHandler::class.java)
    }

}