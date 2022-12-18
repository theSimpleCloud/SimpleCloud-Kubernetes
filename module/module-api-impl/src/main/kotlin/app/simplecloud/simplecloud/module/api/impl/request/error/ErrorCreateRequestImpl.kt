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

package app.simplecloud.simplecloud.module.api.impl.request.error

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.module.api.error.configuration.ErrorConfiguration
import app.simplecloud.simplecloud.module.api.error.configuration.ErrorCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.InternalErrorService
import app.simplecloud.simplecloud.module.api.request.error.ErrorCreateRequest
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 18.10.22
 * Time: 12:38
 * @author Frederick Baier
 *
 */
class ErrorCreateRequestImpl(
    private val configuration: ErrorCreateConfiguration,
    private val internalService: InternalErrorService,
) : ErrorCreateRequest {

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        internalService.createErrorInternal(
            ErrorConfiguration(
                UUID.randomUUID(),
                configuration.shortMessage,
                configuration.message,
                configuration.processName,
                System.currentTimeMillis(),
                configuration.errorData,
                configuration.resolveFunction
            )
        )
    }

}