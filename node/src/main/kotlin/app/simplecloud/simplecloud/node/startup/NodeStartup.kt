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

package app.simplecloud.simplecloud.node.startup

import app.simplecloud.simplecloud.database.api.factory.DatabaseFactory
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.node.connect.NodeClusterConnect
import app.simplecloud.simplecloud.node.startup.prepare.NodePreparer
import app.simplecloud.simplecloud.node.startup.prepare.PreparedNode
import app.simplecloud.simplecloud.restserver.api.RestServerConfig
import org.apache.logging.log4j.LogManager


/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 10:23
 * @author Frederick Baier
 */
class NodeStartup(
    private val startArguments: NodeStartArgumentParserMain,
    private val databaseFactory: DatabaseFactory,
    private val distributionFactory: DistributionFactory,
    private val kubeAPI: KubeAPI,
    private val restServerConfig: RestServerConfig
) {

    fun start() {
        val nodePreparer = NodePreparer(
            this.databaseFactory,
            this.kubeAPI,
            this.restServerConfig.setupManager,
            this.restServerConfig.tokenHandlerFactory
        )
        val preparedNode = nodePreparer.prepare()
        executeClusterConnect(preparedNode)
    }

    private fun executeClusterConnect(preparedNode: PreparedNode) {
        try {
            executeClusterConnect0(preparedNode)
        } catch (e: Exception) {
            logger.error("An error occurred while connecting to cluster", e)
        }
    }

    private fun executeClusterConnect0(preparedNode: PreparedNode) {
        NodeClusterConnect(
            this.distributionFactory,
            this.kubeAPI,
            preparedNode.repositories,
            restServerConfig,
            preparedNode.tokenHandler
        ).connect()
    }

    companion object {
        private val logger = LogManager.getLogger(NodeStartup::class.java)
    }

}