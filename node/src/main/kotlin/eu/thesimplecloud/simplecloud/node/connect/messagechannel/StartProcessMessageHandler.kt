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
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Key
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteNodeRepository
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import eu.thesimplecloud.simplecloud.api.node.Node
import eu.thesimplecloud.simplecloud.api.node.configuration.NodeConfiguration
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import eu.thesimplecloud.simplecloud.api.service.CloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.service.NodeService
import eu.thesimplecloud.simplecloud.api.utils.NetworkComponent
import eu.thesimplecloud.simplecloud.container.container.IContainer
import eu.thesimplecloud.simplecloud.container.image.IImage
import eu.thesimplecloud.simplecloud.node.annotation.NodeName
import eu.thesimplecloud.simplecloud.node.service.CloudProcessServiceImpl
import eu.thesimplecloud.simplecloud.node.task.CloudProcessCreationTask
import eu.thesimplecloud.simplecloud.node.task.ProcessStartTask
import eu.thesimplecloud.simplecloud.node.util.SelfNodeGetter
import eu.thesimplecloud.simplecloud.node.util.UncaughtExceptions
import java.util.concurrent.CompletableFuture

class StartProcessMessageHandler @Inject constructor(
    private val injector: Injector,
    private val nodeService: NodeService,
    private val nodeRepository: IgniteNodeRepository,
    private val processService: CloudProcessServiceImpl,
    private val groupService: CloudProcessGroupService,
    private val processFactory: CloudProcessFactory,
) : MessageHandler<ProcessStartConfiguration, CloudProcess> {

    override fun handleMessage(
        message: ProcessStartConfiguration,
        sender: NetworkComponent
    ): CompletableFuture<CloudProcess> {
        val process = await(createProcess(message))
        await(updateProcessToCluster(process))
        await(increaseUsedMemoryOnSelfNode(process.getMaxMemory()))
        startProcess(process)
        return completedFuture(process)
    }

    private fun updateProcessToCluster(process: CloudProcess): CompletableFuture<Unit> {
        return this.processService.updateProcessToCluster(process)
    }

    private fun createProcess(configuration: ProcessStartConfiguration): CompletableFuture<CloudProcess> {
        return CloudProcessCreationTask(
            configuration,
            this.processService,
            this.groupService,
            this.nodeService,
            this.processFactory,
            this.injector.getInstance(Key.get(String::class.java, NodeName::class.java))
        ).run()
    }

    private fun startProcess(process: CloudProcess) {
        ProcessStartTask(
            process,
            this.injector.getInstance(IContainer.Factory::class.java),
            this.injector.getInstance(IImage.Factory::class.java),
            this.injector
        ).run().exceptionally { UncaughtExceptions.handle(it) }
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

    private fun getSelfNode(): CompletableFuture<Node> {
        val selfNodeGetter = this.injector.getInstance(SelfNodeGetter::class.java)
        return selfNodeGetter.getSelfNode()
    }

    private fun updateNode(configuration: NodeConfiguration): CompletableFuture<Unit> {
        return this.nodeRepository.save(configuration.name, configuration)
    }


}