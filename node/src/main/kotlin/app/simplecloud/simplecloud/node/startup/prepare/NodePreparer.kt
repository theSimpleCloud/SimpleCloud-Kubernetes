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

package app.simplecloud.simplecloud.node.startup.prepare

import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.database.api.DatabaseOfflineCloudPlayerRepository
import app.simplecloud.simplecloud.database.api.factory.DatabaseFactory
import app.simplecloud.simplecloud.database.api.factory.DatabaseRepositories
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.module.api.impl.LocalAPIImpl
import app.simplecloud.simplecloud.node.connect.RestTokenLoader
import app.simplecloud.simplecloud.node.startup.prepare.database.DatabaseSafeStarter
import app.simplecloud.simplecloud.node.startup.prepare.module.NodeModuleLoader
import app.simplecloud.simplecloud.restserver.api.auth.token.TokenHandler
import app.simplecloud.simplecloud.restserver.api.auth.token.TokenHandlerFactory
import app.simplecloud.simplecloud.restserver.api.setup.RestSetupManager
import org.apache.logging.log4j.LogManager

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 18:12
 * @author Frederick Baier
 */
class NodePreparer(
    private val databaseFactory: DatabaseFactory,
    private val kubeApi: KubeAPI,
    private val restSetupManager: RestSetupManager,
    private val tokenHandlerFactory: TokenHandlerFactory,
    private val environmentVariables: EnvironmentVariables,
) {

    private val localAPI = LocalAPIImpl()

    fun prepare(): PreparedNode {
        logger.info("Starting Node...")
        val databaseRepositories = initDatabaseRepositories()
        val restToken = RestTokenLoader(this.kubeApi.getSecretService()).loadRestToken()
        val jwtTokenHandler = this.tokenHandlerFactory.create(restToken)
        checkForAnyWebAccount(databaseRepositories.offlineCloudPlayerRepository, jwtTokenHandler)
        setupEnd()
        val nodeModuleLoader = loadModules()
        logger.info("Node Startup completed")
        return PreparedNode(
            databaseRepositories,
            jwtTokenHandler,
            nodeModuleLoader,
            this.localAPI,
            this.environmentVariables
        )
    }

    private fun loadModules(): NodeModuleLoader {
        val moduleLoader = NodeModuleLoader(this.localAPI)
        moduleLoader.loadModules()
        return moduleLoader
    }

    private fun checkForAnyWebAccount(
        offlineCloudPlayerRepository: DatabaseOfflineCloudPlayerRepository,
        tokenHandler: TokenHandler,
    ) {
        FirstAccountCheck(
            offlineCloudPlayerRepository,
            tokenHandler,
            this.restSetupManager
        ).checkForAccount()
    }

    private fun setupEnd() {
        this.restSetupManager.onEndOfAllSetups()
    }

    private fun initDatabaseRepositories(): DatabaseRepositories {
        return DatabaseSafeStarter(this.restSetupManager, this.kubeApi.getSecretService(), this.databaseFactory)
            .connectToDatabase()
    }

    companion object {
        private val logger = LogManager.getLogger(NodePreparer::class.java)
    }

}