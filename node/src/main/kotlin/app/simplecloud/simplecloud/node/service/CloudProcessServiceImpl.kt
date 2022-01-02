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

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudProcessService
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.node.process.InternalProcessStartHandler
import app.simplecloud.simplecloud.node.process.ProcessStarter
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.concurrent.CompletableFuture

@Singleton
class CloudProcessServiceImpl @Inject constructor(
    processFactory: CloudProcessFactory,
    igniteRepository: IgniteCloudProcessRepository,
    private val processStarterFactory: ProcessStarter.Factory
) : AbstractCloudProcessService(
    processFactory, igniteRepository
) {
    override fun startNewProcessInternal(configuration: ProcessStartConfiguration): CompletableFuture<CloudProcess> {
        return InternalProcessStartHandler(this.processStarterFactory, this.igniteRepository, configuration)
            .startProcess()
    }

    override fun shutdownProcessInternal(process: CloudProcess): CompletableFuture<Unit> {
        TODO("Not yet implemented")
    }
}