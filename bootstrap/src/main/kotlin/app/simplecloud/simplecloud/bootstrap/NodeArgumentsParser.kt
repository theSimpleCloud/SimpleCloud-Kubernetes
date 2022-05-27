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

package app.simplecloud.simplecloud.bootstrap

import app.simplecloud.simplecloud.database.mongo.factory.MongoDatabaseFactory
import app.simplecloud.simplecloud.distibution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.kubernetes.impl.KubeImplAPI
import app.simplecloud.simplecloud.node.startup.NodeStartup
import app.simplecloud.simplecloud.restserver.api.RestServerConfig
import app.simplecloud.simplecloud.restserver.api.auth.NoAuthService
import app.simplecloud.simplecloud.restserver.base.RestServerFactory
import app.simplecloud.simplecloud.restserver.impl.auth.JwtTokenHandlerFactory
import app.simplecloud.simplecloud.restserver.impl.controller.ControllerHandlerFactoryImpl
import app.simplecloud.simplecloud.restserver.impl.setup.RestSetupManagerImpl
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 10:08
 * @author Frederick Baier
 */
class NodeArgumentsParser : CliktCommand() {

    val mongoDbConnectionString: String? by option(help = "Sets the connection string for MongoDB")

    override fun run() {
        val restServer = RestServerFactory.createRestServer(NoAuthService(), 8008)
        NodeStartup(
            MongoDatabaseFactory(),
            HazelcastDistributionFactory(),
            KubeImplAPI(),
            NodeSelfPod(),
            RestServerConfig(
                restServer,
                JwtTokenHandlerFactory(),
                ControllerHandlerFactoryImpl(),
                RestSetupManagerImpl(restServer)
            )
        ).start()
    }

}