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

package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.api.impl.guice.CloudAPIBinderModule
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.kubernetes.api.OtherNodeAddressGetter
import app.simplecloud.simplecloud.node.service.*
import app.simplecloud.simplecloud.node.startup.guice.NodeBinderModule
import app.simplecloud.simplecloud.node.startup.task.RestServerStartTask
import app.simplecloud.simplecloud.node.task.NodeOnlineProcessesChecker
import app.simplecloud.simplecloud.restserver.base.RestServer
import com.google.inject.Injector
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class NodeClusterConnect @Inject constructor(
    private val injector: Injector,
    private val distributionFactory: DistributionFactory
) {

    private val nodeBindPort = 1670

    fun connect(){
        logger.info("Connecting to cluster...")
        val distribution = startDistribution()
        val finalInjector = createFinalInjector(distribution)
        startRestServer(finalInjector)
        registerMessageChannels(finalInjector)
        checkForFirstNodeInCluster(finalInjector, distribution)
        checkOnlineProcesses(finalInjector)
    }

    private fun registerMessageChannels(injector: Injector){
        val messageChannelsInitializer = injector.getInstance(MessageChannelsInitializer::class.java)
        messageChannelsInitializer.initializeMessageChannels()
    }

    private fun checkOnlineProcesses(injector: Injector) {
        logger.info("Checking for online tasks")
        val nodeOnlineProcessesChecker = injector.getInstance(NodeOnlineProcessesChecker::class.java)
        runBlocking {
            nodeOnlineProcessesChecker.checkOnlineCount()
        }
    }

    private fun checkForFirstNodeInCluster(injector: Injector, distribution: Distribution) {
        if (distribution.getServers().size == 1) {
            val nodeRepositoriesInitializer = injector.getInstance(NodeRepositoriesInitializer::class.java)
            nodeRepositoriesInitializer.initializeRepositories()
        }
    }

    private fun startRestServer(injector: Injector): CompletableFuture<RestServer> {
        return injector.getInstance(RestServerStartTask::class.java).run()
    }

    private fun createFinalInjector(distribution: Distribution): Injector {
        val cloudAPIBinderModule = CloudAPIBinderModule(
            distribution,
            NodeServiceImpl::class.java,
            CloudProcessServiceImpl::class.java,
            CloudProcessGroupServiceImpl::class.java,
            CloudPlayerServiceImpl::class.java,
            PermissionGroupServiceImpl::class.java
        )
        return injector.createChildInjector(
            NodeBinderModule(),
            cloudAPIBinderModule
        )
    }

    private fun startDistribution(): Distribution {
        val addresses = getOtherNodesAddressesToConnectTo()
        logger.info("Connecting to {}", addresses)
        return this.distributionFactory.createServer(this.nodeBindPort, addresses)
    }

    private fun getOtherNodesAddressesToConnectTo(): List<Address> {
        val otherNodeAddressGetter = this.injector.getInstance(OtherNodeAddressGetter::class.java)
        return otherNodeAddressGetter.getOtherNodeAddresses()
    }

    companion object {
        private val logger = LogManager.getLogger(NodeClusterConnect::class.java)
    }

}