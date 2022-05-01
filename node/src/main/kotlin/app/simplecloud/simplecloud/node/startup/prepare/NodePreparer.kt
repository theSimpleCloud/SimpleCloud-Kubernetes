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

import app.simplecloud.simplecloud.database.api.DatabaseOfflineCloudPlayerRepository
import app.simplecloud.simplecloud.database.api.factory.DatabaseFactory
import app.simplecloud.simplecloud.database.api.factory.DatabaseRepositories
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.node.connect.JwtTokenLoader
import app.simplecloud.simplecloud.node.startup.prepare.database.DatabaseSafeStarter
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
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
    private val restSetupManager: RestSetupManager
) {

    fun prepare(): PreparedNode {
        logger.info("Starting Node...")
        val databaseRepositories = initDatabaseRepositories()
        val jwtTokenHandler = JwtTokenHandler(JwtTokenLoader(this.kubeApi.getSecretService()).loadJwtToken())
        checkForAnyWebAccount(databaseRepositories.offlineCloudPlayerRepository, jwtTokenHandler)
        setupEnd()
        logger.info("Node Startup completed")
        return PreparedNode(databaseRepositories, jwtTokenHandler)
    }

    private fun checkForAnyWebAccount(
        offlineCloudPlayerRepository: DatabaseOfflineCloudPlayerRepository,
        jwtTokenHandler: JwtTokenHandler
    ) {
        FirstAccountCheck(
            offlineCloudPlayerRepository,
            jwtTokenHandler,
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