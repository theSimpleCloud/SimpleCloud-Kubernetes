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

import app.simplecloud.simplecloud.database.mongo.factory.MongoDatabaseFactory
import app.simplecloud.simplecloud.distibution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.kubernetes.impl.KubeImplAPI
import app.simplecloud.simplecloud.restserver.api.RestServerConfig
import app.simplecloud.simplecloud.restserver.api.auth.NoAuthService
import app.simplecloud.simplecloud.restserver.base.RestServerFactory
import app.simplecloud.simplecloud.restserver.impl.auth.JwtTokenHandlerFactory
import app.simplecloud.simplecloud.restserver.impl.controller.ControllerHandlerFactoryImpl
import app.simplecloud.simplecloud.restserver.impl.setup.RestSetupManagerImpl
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 10:08
 * @author Frederick Baier
 */
class NodeStartArgumentParserMain : CliktCommand() {

    val webinterfaceMode: WebinterfaceMode by option(help = "Sets the mode for the webinterface")
        .enum<WebinterfaceMode>()
        .default(WebinterfaceMode.DOCKER)

    val mongoDbConnectionString: String? by option(help = "Sets the connection string for MongoDB")

    val bindAddress: Address? by option(help = "Sets the address for the node to bind to").convert { Address.fromIpString(it) }
    val maxMemory: Int? by option(help = "Let the node generate a random name").int()
    val randomNodeName: Boolean by option(help = "Let the node generate a random name").flag()


    override fun run() {
        val restServer = RestServerFactory.createRestServer(NoAuthService(), 8008)
        NodeStartup(
            this,
            MongoDatabaseFactory(),
            HazelcastDistributionFactory(),
            KubeImplAPI(),
            RestServerConfig(
                restServer,
                JwtTokenHandlerFactory(),
                ControllerHandlerFactoryImpl(),
                RestSetupManagerImpl(restServer)
            )
        ).start()
    }

}