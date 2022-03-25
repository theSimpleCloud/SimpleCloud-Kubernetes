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

package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.api.impl.guice.CloudAPIBinderModule
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteNodeRepository
import app.simplecloud.simplecloud.api.impl.util.ClusterKey
import app.simplecloud.simplecloud.api.impl.util.SingleInstanceBinderModule
import app.simplecloud.simplecloud.api.node.configuration.NodeConfiguration
import app.simplecloud.simplecloud.api.utils.Address
import app.simplecloud.simplecloud.ignite.bootstrap.IgniteBuilder
import app.simplecloud.simplecloud.kubernetes.api.OtherNodeAddressGetter
import app.simplecloud.simplecloud.node.connect.clusterkey.ClusterKeyEntity
import app.simplecloud.simplecloud.node.repository.mongo.MongoSingleObjectRepository
import app.simplecloud.simplecloud.node.service.*
import app.simplecloud.simplecloud.node.startup.guice.NodeBinderModule
import app.simplecloud.simplecloud.node.startup.task.RestServerStartTask
import app.simplecloud.simplecloud.node.task.NodeOnlineProcessesChecker
import app.simplecloud.simplecloud.restserver.base.RestServer
import com.google.inject.Injector
import dev.morphia.Datastore
import kotlinx.coroutines.runBlocking
import org.apache.ignite.Ignite
import org.apache.ignite.plugin.security.SecurityCredentials
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class NodeClusterConnect @Inject constructor(
    private val injector: Injector,
    private val datastore: Datastore,
) {

    private val nodeBindAddress = Address.fromIpString("127.0.0.1:1670")

    fun connect(){
        logger.info("Connecting to cluster...")
        val clusterKey = loadClusterKey()
        val ignite = startIgnite(clusterKey)
        val finalInjector = createFinalInjector(ignite, clusterKey)
        startRestServer(finalInjector)
        registerMessageChannels(finalInjector)
        checkForFirstNodeInCluster(finalInjector, ignite)
        writeSelfNodeInRepository(finalInjector, ignite)
        checkOnlineProcesses(finalInjector)
    }

    private fun registerMessageChannels(injector: Injector){
        val messageChannelsInitializer = injector.getInstance(MessageChannelsInitializer::class.java)
        messageChannelsInitializer.initializeMessageChannels()
    }

    private fun writeSelfNodeInRepository(injector: Injector, ignite: Ignite) {
        logger.info("Writing Self-Node into Cluster-Cache")
        SelfNodeWriter(
            injector.getInstance(IgniteNodeRepository::class.java),
            NodeConfiguration(
                this.nodeBindAddress,
                ignite.cluster().localNode().id(),
            )
        ).writeSelfNode()
    }

    private fun checkOnlineProcesses(injector: Injector) {
        logger.info("Checking for online tasks")
        val nodeOnlineProcessesChecker = injector.getInstance(NodeOnlineProcessesChecker::class.java)
        runBlocking {
            nodeOnlineProcessesChecker.checkOnlineCount()
        }
    }

    private fun checkForFirstNodeInCluster(injector: Injector, ignite: Ignite) {
        if (ignite.cluster().nodes().size == 1) {
            val nodeRepositoriesInitializer = injector.getInstance(NodeRepositoriesInitializer::class.java)
            nodeRepositoriesInitializer.initializeRepositories()
        }
    }

    private fun startRestServer(injector: Injector): CompletableFuture<RestServer> {
        return injector.getInstance(RestServerStartTask::class.java).run()
    }

    private fun createFinalInjector(ignite: Ignite, clusterKey: ClusterKey): Injector {
        val cloudAPIBinderModule = CloudAPIBinderModule(
            ignite,
            NodeServiceImpl::class.java,
            CloudProcessServiceImpl::class.java,
            CloudProcessGroupServiceImpl::class.java,
            CloudPlayerServiceImpl::class.java,
            PermissionGroupServiceImpl::class.java
        )
        return injector.createChildInjector(
            NodeBinderModule(),
            cloudAPIBinderModule,
            SingleInstanceBinderModule(ClusterKey::class.java, clusterKey)
        )
    }

    private fun startIgnite(clusterKey: ClusterKey): Ignite {
        val addresses = getOtherNodesAddressesToConnectTo()
        logger.info("Connecting to {}", addresses)
        val securityCredentials = SecurityCredentials(clusterKey.login, clusterKey.password)
        val igniteBuilder = IgniteBuilder(this.nodeBindAddress, false, securityCredentials)
            .withAddressesToConnectTo(*addresses.toTypedArray())
        return igniteBuilder.start()
    }

    private fun loadClusterKey(): ClusterKey {
        val clusterKeyRepo = MongoSingleObjectRepository(
            this.datastore,
            ClusterKeyEntity::class.java,
            ClusterKeyEntity.KEY
        )
        val clusterKeyEntity = clusterKeyRepo.loadObject().join()
        return ClusterKey(clusterKeyEntity.login, clusterKeyEntity.password)
    }

    private fun getOtherNodesAddressesToConnectTo(): List<Address> {
        val otherNodeAddressGetter = this.injector.getInstance(OtherNodeAddressGetter::class.java)
        return otherNodeAddressGetter.getOtherNodeAddresses()
    }

    companion object {
        private val logger = LogManager.getLogger(NodeClusterConnect::class.java)
    }

}