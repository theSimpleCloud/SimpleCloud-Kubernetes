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

package app.simplecloud.simplecloud.node.startup.task

import app.simplecloud.simplecloud.api.impl.util.SingleInstanceBinderModule
import app.simplecloud.simplecloud.database.api.factory.DatabaseFactory
import app.simplecloud.simplecloud.database.api.factory.DatabaseRepositories
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.kubernetes.api.secret.SecretSpec
import app.simplecloud.simplecloud.node.startup.NodeStartArgumentParserMain
import app.simplecloud.simplecloud.node.startup.task.database.DatabaseRepositoriesModule
import app.simplecloud.simplecloud.node.startup.task.database.DatabaseSafeStarter
import app.simplecloud.simplecloud.restserver.auth.AuthServiceProvider
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.base.RestServer
import app.simplecloud.simplecloud.restserver.base.RestServerAPI
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import app.simplecloud.simplecloud.restserver.base.service.NoAuthService
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import com.google.inject.Guice
import com.google.inject.Injector
import org.apache.commons.lang3.RandomStringUtils
import org.apache.logging.log4j.LogManager

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 18:12
 * @author Frederick Baier
 */
class NodePreparer(
    private val startArguments: NodeStartArgumentParserMain,
    private val databaseFactory: DatabaseFactory,
    private val kubeApi: KubeAPI
) {

    private val restServer = RestServerAPI.createRestServer(NoAuthService(), 8008)
    private val restSetupManager = RestSetupManager(this.restServer)

    private val jwtTokenHandler = initJwtTokenHandler()
    private val databaseRepositories = initDatabaseRepositories()
    private val injector = createInjector()

    fun prepare(): Injector {
        logger.info("Starting Node...")
        checkForAnyWebAccount()
        setupEnd()
        logger.info("Node Startup completed")
        return injector
    }

    private fun checkForAnyWebAccount() {
        FirstAccountCheck(
            this.databaseRepositories.offlineCloudPlayerRepository,
            this.jwtTokenHandler,
            this.restSetupManager
        ).checkForAccount()
    }

    private fun initJwtTokenHandler(): JwtTokenHandler {
        return JwtTokenHandler(loadJwtSecret())
    }

    private fun loadJwtSecret(): String {
        val secretService = this.kubeApi.getSecretService()
        return try {
            secretService.getSecret("jwt").getStringValueOf("jwt")
        } catch (e: NoSuchElementException) {
            createJwtSecret()
        }
    }

    private fun createJwtSecret(): String {
        val secretService = this.kubeApi.getSecretService()
        val secretString = RandomStringUtils.randomAlphanumeric(32)
        secretService.createSecret("jwt", SecretSpec().withData("jwt", secretString))
        return secretString
    }

    private fun setupEnd() {
        this.restSetupManager.onEndOfAllSetups()
    }

    private fun createInjector(): Injector {
        return Guice.createInjector(
            DatabaseRepositoriesModule(this.databaseRepositories),
            KubeBinderModule(this.kubeApi),
            SingleInstanceBinderModule(RestServer::class.java, this.restServer),
            SingleInstanceBinderModule(RestSetupManager::class.java, this.restSetupManager),
            SingleInstanceBinderModule(JwtTokenHandler::class.java, this.jwtTokenHandler),
            SingleInstanceBinderModule(AuthServiceProvider::class.java, object : AuthServiceProvider {
                override fun getAuthService(): AuthService {
                    return restServer.getAuthService()
                }
            }),
        )
    }

    private fun initDatabaseRepositories(): DatabaseRepositories {
        return DatabaseSafeStarter(this.restSetupManager, this.kubeApi.getSecretService(), this.databaseFactory)
            .connectToDatabase()
    }

    companion object {
        private val logger = LogManager.getLogger(NodePreparer::class.java)
    }

}