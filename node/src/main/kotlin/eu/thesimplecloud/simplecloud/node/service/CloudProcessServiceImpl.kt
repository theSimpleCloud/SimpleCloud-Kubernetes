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

package eu.thesimplecloud.simplecloud.node.service

import com.ea.async.Async.await
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.impl.process.factory.ICloudProcessFactory
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import eu.thesimplecloud.simplecloud.api.impl.service.AbstractCloudProcessService
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.service.ICloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.service.INodeService
import eu.thesimplecloud.simplecloud.container.IContainer
import eu.thesimplecloud.simplecloud.container.IImage
import eu.thesimplecloud.simplecloud.node.annotation.NodeName
import eu.thesimplecloud.simplecloud.node.process.MultiNodeProcessStarter
import eu.thesimplecloud.simplecloud.node.process.container.IContainerProcessStarter
import eu.thesimplecloud.simplecloud.node.task.CloudProcessCreationTask
import eu.thesimplecloud.simplecloud.node.task.NodeToStartProcessSelectionTask
import eu.thesimplecloud.simplecloud.node.task.ProcessStartTask
import eu.thesimplecloud.simplecloud.node.util.UncaughtExceptions
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitter
import java.util.concurrent.CompletableFuture

@Singleton
class CloudProcessServiceImpl @Inject constructor(
    processFactory: ICloudProcessFactory,
    igniteRepository: IgniteCloudProcessRepository,
    private val taskSubmitter: TaskSubmitter,
    private val groupService: ICloudProcessGroupService,
    private val nodeService: INodeService,
    private val injector: Injector
) : AbstractCloudProcessService(
    processFactory, igniteRepository
) {
    override fun startNewProcessInternal(configuration: ProcessStartConfiguration): CompletableFuture<ICloudProcess> {
        val process = await(createProcess(configuration))
        await(updateProcessToCluster(process))
        MultiNodeProcessStarter(this.taskSubmitter, this.nodeService, process, this.injector).startProcess()
        return completedFuture(process)
    }

    private fun updateProcessToCluster(process: ICloudProcess): CompletableFuture<Unit> {
        return this.igniteRepository.save(process.getName(), process.toConfiguration())
    }

    private fun createProcess(configuration: ProcessStartConfiguration): CompletableFuture<ICloudProcess> {
        return this.taskSubmitter.submit(
            CloudProcessCreationTask(
                configuration,
                this,
                this.groupService,
                this.nodeService,
                this.processFactory,
                this.injector.getInstance(Key.get(String::class.java, NodeName::class.java))
            )
        )
    }

    override fun shutdownProcessInternal(process: ICloudProcess): CompletableFuture<Unit> {
        TODO("Not yet implemented")
    }
}