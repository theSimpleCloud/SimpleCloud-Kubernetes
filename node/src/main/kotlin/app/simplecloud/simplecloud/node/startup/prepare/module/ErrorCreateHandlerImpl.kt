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
import app.simplecloud.simplecloud.api.service.ErrorService
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 23.10.22
 * Time: 10:04
 * @author Frederick Baier
 *
 */
class ErrorCreateHandlerImpl : ErrorCreateHandler {

    @Volatile
    private var errorService: ErrorService? = null

    private val waitingErrors = CopyOnWriteArrayList<ErrorCreateConfiguration>()

    override fun create(errorCreateConfiguration: ErrorCreateConfiguration) {
        val errorService = this.errorService
        if (errorService == null) {
            this.waitingErrors.add(errorCreateConfiguration)
        } else {
            createError(errorCreateConfiguration)
        }
    }

    override fun setErrorService(errorService: ErrorService) {
        if (this.errorService != null)
            throw IllegalStateException("Cannot set ErrorService twice")

        this.errorService = errorService

        createWaitingErrors()
    }

    private fun createWaitingErrors() {
        this.waitingErrors.forEach {
            createError(it)
        }
        this.waitingErrors.clear()
    }

    private fun createError(configuration: ErrorCreateConfiguration) {
        this.errorService?.createCreateRequest(configuration)?.submit()
    }

}