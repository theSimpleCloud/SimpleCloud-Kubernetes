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

package app.simplecloud.simplecloud.module.api.service

import app.simplecloud.simplecloud.api.service.Service
import app.simplecloud.simplecloud.module.api.error.Error
import app.simplecloud.simplecloud.module.api.error.ErrorTypeFixedChecker
import app.simplecloud.simplecloud.module.api.error.configuration.ErrorCreateConfiguration
import app.simplecloud.simplecloud.module.api.request.error.ErrorCreateRequest
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 10.10.22
 * Time: 12:57
 * @author Frederick Baier
 *
 */
interface ErrorService : Service {

    /**
     * Returns all errors associated with the specified [processName]
     */
    fun findByProcessName(processName: String): CompletableFuture<List<Error>>

    /**
     * Returns all created errors
     */
    fun findAll(): CompletableFuture<List<Error>>

    /**
     * Returns the error found by the specified id
     */
    fun findById(id: UUID): CompletableFuture<Error>

    /**
     * Creates a request to create a new error
     */
    fun createCreateRequest(errorConfiguration: ErrorCreateConfiguration): ErrorCreateRequest

    /**
     *  Registers an error type and it's checker to determine weather an error of that type has been fixed
     */
    fun registerErrorType(errorTye: Int, checker: ErrorTypeFixedChecker)

}