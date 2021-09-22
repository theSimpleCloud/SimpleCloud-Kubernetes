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

package eu.thesimplecloud.simplecloud.node.connect.messagechannel

import com.ea.async.Async.await
import com.google.inject.Injector
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteNodeRepository
import eu.thesimplecloud.simplecloud.api.messagechannel.handler.IMessageHandler
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.node.configuration.NodeConfiguration
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.repository.INodeRepository
import eu.thesimplecloud.simplecloud.api.service.ICloudProcessService
import eu.thesimplecloud.simplecloud.api.service.INodeService
import eu.thesimplecloud.simplecloud.api.utils.INetworkComponent
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import eu.thesimplecloud.simplecloud.container.IContainer
import eu.thesimplecloud.simplecloud.container.IImage
import eu.thesimplecloud.simplecloud.node.process.container.IContainerProcessStarter
import eu.thesimplecloud.simplecloud.node.task.ProcessStartTask
import eu.thesimplecloud.simplecloud.node.util.SelfNodeGetter
import eu.thesimplecloud.simplecloud.node.util.UncaughtExceptions
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitter
import java.util.concurrent.CompletableFuture

class StartProcessMessageHandler(
    private val injector: Injector
) : IMessageHandler<String, Unit> {

    private val nodeService: INodeService = this.injector.getInstance(INodeService::class.java)
    private val taskSubmitter: TaskSubmitter = this.injector.getInstance(TaskSubmitter::class.java)
    private val processService: ICloudProcessService = this.injector.getInstance(ICloudProcessService::class.java)
    private val nodeRepository: INodeRepository = this.injector.getInstance(IgniteNodeRepository::class.java)

    override fun handleMessage(message: String, sender: INetworkComponent): CompletableFuture<Unit> {
        val process = await(this.processService.findProcessByName(message))
        await(increaseUsedMemoryOnSelfNode(process.getMaxMemory()))
        startProcess(process)
        return unitFuture()
    }

    private fun startProcess(process: ICloudProcess): CompletableFuture<Unit> {
        this.taskSubmitter.submit(
            ProcessStartTask(
                process,
                this.injector.getInstance(IContainer.Factory::class.java),
                this.injector.getInstance(IImage.Factory::class.java),
                this.injector.getInstance(IContainerProcessStarter::class.java)
            )
        ).exceptionally { UncaughtExceptions.handle(it) }
        return unitFuture()
    }

    private fun increaseUsedMemoryOnSelfNode(increaseAmount: Int): CompletableFuture<Unit> {
        val selfNode = await(getSelfNode())
        val updatedNodeConfiguration = increaseNodeConfigurationMemory(selfNode.toConfiguration(), increaseAmount)
        await(updateNode(updatedNodeConfiguration))
        return unitFuture()
    }

    private fun increaseNodeConfigurationMemory(
        currentConfiguration: NodeConfiguration,
        increaseAmount: Int
    ): NodeConfiguration {
        return NodeConfiguration(
            currentConfiguration.address,
            currentConfiguration.name,
            currentConfiguration.igniteId,
            currentConfiguration.maxMemoryInMB,
            currentConfiguration.usedMemoryInMB + increaseAmount
        )
    }

    private fun getSelfNode(): CompletableFuture<INode> {
        val selfNodeGetter = this.injector.getInstance(SelfNodeGetter::class.java)
        return selfNodeGetter.getSelfNode()
    }

    private fun updateNode(configuration: NodeConfiguration): CompletableFuture<Unit> {
        return this.nodeRepository.save(configuration.name, configuration)
    }


}