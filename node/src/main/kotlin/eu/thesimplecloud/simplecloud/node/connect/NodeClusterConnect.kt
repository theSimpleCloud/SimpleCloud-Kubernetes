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

package eu.thesimplecloud.simplecloud.node.connect

import com.ea.async.Async.await
import com.google.inject.Injector
import dev.morphia.Datastore
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.impl.guice.CloudAPIBinderModule
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteNodeRepository
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import eu.thesimplecloud.simplecloud.api.node.configuration.NodeConfiguration
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import eu.thesimplecloud.simplecloud.api.utils.Address
import eu.thesimplecloud.simplecloud.ignite.bootstrap.IgniteBuilder
import eu.thesimplecloud.simplecloud.node.annotation.NodeBindAddress
import eu.thesimplecloud.simplecloud.node.annotation.NodeMaxMemory
import eu.thesimplecloud.simplecloud.node.annotation.NodeName
import eu.thesimplecloud.simplecloud.api.impl.util.ClusterKey
import eu.thesimplecloud.simplecloud.node.connect.messagechannel.StartProcessMessageHandler
import eu.thesimplecloud.simplecloud.node.mongo.node.MongoPersistentNodeRepository
import eu.thesimplecloud.simplecloud.node.mongo.node.PersistentNodeEntity
import eu.thesimplecloud.simplecloud.node.service.*
import eu.thesimplecloud.simplecloud.node.startup.task.RestServerStartTask
import eu.thesimplecloud.simplecloud.node.task.NodeCheckOnlineProcessesTask
import eu.thesimplecloud.simplecloud.node.util.SingleInstanceBinderModule
import eu.thesimplecloud.simplecloud.restserver.RestServer
import org.apache.ignite.Ignite
import org.apache.ignite.plugin.security.SecurityCredentials
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class NodeClusterConnect @Inject constructor(
    private val injector: Injector,
    private val datastore: Datastore,
    @NodeName private val nodeName: String,
    @NodeBindAddress private val nodeBindAddress: Address,
    @NodeMaxMemory private val nodeMaxMemory: Int,
) {

    fun run(): CompletableFuture<Unit> {
        val nodeRepository = MongoPersistentNodeRepository(this.datastore)
        await(editOrInsertSelfNode(nodeRepository))
        val clusterKey = await(loadClusterKey())
        val ignite = await(startIgnite(nodeRepository, clusterKey))
        val finalInjector = createFinalInjector(ignite, clusterKey)
        startRestServer(finalInjector)
        await(initializeMessageChannels(finalInjector))
        await(checkForFirstNodeInCluster(finalInjector, ignite))
        await(writeSelfNodeInRepository(finalInjector, ignite))
        await(checkOnlineProcesses(finalInjector))
        return unitFuture()
    }

    private fun initializeMessageChannels(injector: Injector): CompletableFuture<Unit> {
        val messageChannelManager = injector.getInstance(MessageChannelManager::class.java)
        messageChannelManager.registerMessageChannel<ProcessStartConfiguration, CloudProcess>("start_process")
            .setMessageHandler(injector.getInstance(StartProcessMessageHandler::class.java))
        return unitFuture()
    }

    private fun writeSelfNodeInRepository(injector: Injector, ignite: Ignite): CompletableFuture<Unit> {
        return SelfNodeWriteTask(
            injector.getInstance(IgniteNodeRepository::class.java),
            NodeConfiguration(
                this.nodeBindAddress,
                this.nodeName,
                ignite.cluster().localNode().id(),
                this.nodeMaxMemory,
                0
            )
        ).run()
    }

    private fun checkOnlineProcesses(injector: Injector): CompletableFuture<Unit> {
        val nodeCheckOnlineProcessesTask = injector.getInstance(NodeCheckOnlineProcessesTask::class.java)
        return nodeCheckOnlineProcessesTask.run()
    }

    private fun checkForFirstNodeInCluster(injector: Injector, ignite: Ignite): CompletableFuture<Unit> {
        if (ignite.cluster().nodes().size == 1) {
            val nodeInitRepositoriesTask = injector.getInstance(NodeInitRepositoriesTask::class.java)
            nodeInitRepositoriesTask.run()
        }
        return unitFuture()
    }

    private fun startRestServer(injector: Injector): CompletableFuture<RestServer> {
        return RestServerStartTask(injector).run()
    }

    private fun createFinalInjector(ignite: Ignite, clusterKey: ClusterKey): Injector {
        val cloudAPIBinderModule = CloudAPIBinderModule(
            ignite,
            JvmArgumentsServiceImpl::class.java,
            NodeServiceImpl::class.java,
            ProcessVersionServiceImpl::class.java,
            TemplateServiceImpl::class.java,
            CloudProcessServiceImpl::class.java,
            CloudProcessGroupServiceImpl::class.java
        )
        return injector.createChildInjector(
            cloudAPIBinderModule,
            //SingleClassBinderModule(IContainerProcessStarter::class.java, MountingContainerProcessStarter::class.java),
            SingleInstanceBinderModule(ClusterKey::class.java, clusterKey)
        )
    }

    private fun startIgnite(
        nodeRepository: MongoPersistentNodeRepository,
        clusterKey: ClusterKey
    ): CompletableFuture<Ignite> {
        val addresses = await(getOtherNodesAddressesToConnectTo(nodeRepository))
        val securityCredentials = SecurityCredentials(clusterKey.login, clusterKey.password)
        val igniteBuilder = IgniteBuilder(this.nodeBindAddress, false, securityCredentials)
            .withAddressesToConnectTo(*addresses.toTypedArray())
        return completedFuture(igniteBuilder.start())
    }

    private fun loadClusterKey(): CompletableFuture<ClusterKey> {
        val clusterKeyEntity = await(NodeClusterKeyLoader(this.datastore).loadClusterKey())
        return completedFuture(ClusterKey(clusterKeyEntity.login, clusterKeyEntity.password))
    }

    private fun getOtherNodesAddressesToConnectTo(nodeRepository: MongoPersistentNodeRepository): CompletableFuture<List<Address>> {
        val allNodes = await(nodeRepository.findAll())
        val allNodesWithoutSelfNode = allNodes.filter { it.name != this.nodeName }
        return completedFuture(allNodesWithoutSelfNode.map { it.address })
    }

    private fun editOrInsertSelfNode(nodeRepository: MongoPersistentNodeRepository): CompletableFuture<Unit> {
        val nodeEntity = createNewConnectedSelfNodeEntity()
        return nodeRepository.save(this.nodeName, nodeEntity)
    }

    private fun createNewConnectedSelfNodeEntity(): PersistentNodeEntity {
        return PersistentNodeEntity(this.nodeName, this.nodeBindAddress, true)
    }

}