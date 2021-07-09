/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.api.impl.process.request.group.create

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.flatten
import eu.thesimplecloud.simplecloud.api.future.isCompletedNormally
import eu.thesimplecloud.simplecloud.api.internal.InternalCloudAPI
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalCloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalCloudProcessService
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.validation.GroupConfigurationValidator
import eu.thesimplecloud.simplecloud.api.request.group.create.IProcessGroupCreateRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 01/07/2021
 * Time: 21:35
 * @author Frederick Baier
 */
class ProcessGroupCreateRequest(
    private val internalService: IInternalCloudProcessGroupService,
    private val configuration: AbstractCloudProcessGroupConfiguration
) : IProcessGroupCreateRequest {

    override fun submit(): CompletableFuture<ICloudProcessGroup> {
        if (await(doesGroupExist(configuration.name))) {
            throw IllegalArgumentException("Group already exists")
        }
        return this.internalService.createGroupInternal(configuration)

    }

    private fun doesGroupExist(groupName: String): CompletableFuture<Boolean> {
        val completableFuture = this.internalService.findByName(groupName)
        return completableFuture.handle { _, _ -> completableFuture.isCompletedNormally }
    }

}