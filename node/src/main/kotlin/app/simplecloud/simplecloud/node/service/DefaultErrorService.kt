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

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.module.api.NodeCloudAPI
import app.simplecloud.simplecloud.module.api.error.Error
import app.simplecloud.simplecloud.module.api.error.ErrorFactory
import app.simplecloud.simplecloud.module.api.error.ErrorTypeFixedChecker
import app.simplecloud.simplecloud.module.api.error.configuration.ErrorConfiguration
import app.simplecloud.simplecloud.module.api.error.configuration.ErrorCreateConfiguration
import app.simplecloud.simplecloud.module.api.impl.repository.distributed.DistributedErrorRepository
import app.simplecloud.simplecloud.module.api.impl.request.error.ErrorCreateRequestImpl
import app.simplecloud.simplecloud.module.api.internal.service.InternalErrorService
import app.simplecloud.simplecloud.module.api.request.error.ErrorCreateRequest
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.resource.error.V1Beta1ErrorResourceSpec
import com.google.common.collect.Maps
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 18.10.22
 * Time: 12:25
 * @author Frederick Baier
 *
 */
class DefaultErrorService(
    private val repository: DistributedErrorRepository,
    private val factory: ErrorFactory,
    private val requestHandler: ResourceRequestHandler,
) : InternalErrorService {

    private val errorTypeToChecker = Maps.newConcurrentMap<Int, ErrorTypeFixedChecker>()

    override suspend fun createErrorInternal(configuration: ErrorConfiguration) {
        this.requestHandler.handleCreate(
            "core",
            "Error",
            "v1beta1",
            configuration.id.toString(),
            V1Beta1ErrorResourceSpec(
                configuration.errorType,
                configuration.shortMessage,
                configuration.message,
                configuration.processName,
                configuration.timeStamp,
                configuration.errorData.keys.toTypedArray(),
                configuration.errorData.values.toTypedArray()
            )
        )
        this.repository.save(configuration.id, configuration).await()
    }

    override fun findByProcessName(processName: String): CompletableFuture<List<Error>> {
        val errorsFuture = this.repository.findByProcessName(processName)
        return errorsFuture.thenApply { list -> mapConfigurationListToError(list) }
    }

    override fun findAll(): CompletableFuture<List<Error>> {
        val errorsFuture = this.repository.findAll()
        return errorsFuture.thenApply { list -> mapConfigurationListToError(list) }
    }

    override fun findById(id: UUID): CompletableFuture<Error> {
        val completableFuture = this.repository.find(id)
        return completableFuture.thenApply { this.factory.create(it) }
    }

    override fun createCreateRequest(errorConfiguration: ErrorCreateConfiguration): ErrorCreateRequest {
        return ErrorCreateRequestImpl(errorConfiguration, this)
    }

    override suspend fun deleteResolvedErrors(nodeCloudAPI: NodeCloudAPI) {
        val allErrors = findAll().await()
        allErrors.forEach { deleteErrorIfResolved(it) }
    }

    private suspend fun deleteErrorIfResolved(error: Error) {
        val checker = this.errorTypeToChecker[error.getErrorType()] ?: return
        val isFixed = checker.isErrorFixed(error).await()
        if (isFixed) {
            this.requestHandler.handleDelete("core", "Error", "v1beta1", error.getId().toString())
        }
    }

    private fun mapConfigurationListToError(list: Collection<ErrorConfiguration>): List<Error> {
        return list.map { this.factory.create(it) }
    }

    override fun registerErrorType(errorTye: Int, checker: ErrorTypeFixedChecker) {
        this.errorTypeToChecker[errorTye] = checker
    }

}