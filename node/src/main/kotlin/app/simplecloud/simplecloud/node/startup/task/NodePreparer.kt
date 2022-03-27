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
import app.simplecloud.simplecloud.kubernetes.impl.KubernetesBinderModule
import app.simplecloud.simplecloud.node.repository.mongo.MongoSingleObjectRepository
import app.simplecloud.simplecloud.node.startup.NodeStartArgumentParserMain
import app.simplecloud.simplecloud.node.startup.task.mongo.MongoClientSafeStarter
import app.simplecloud.simplecloud.node.startup.token.TokenSecretEntity
import app.simplecloud.simplecloud.restserver.auth.AuthServiceProvider
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.base.RestServer
import app.simplecloud.simplecloud.restserver.base.RestServerAPI
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import app.simplecloud.simplecloud.restserver.base.service.NoAuthService
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import com.google.inject.Guice
import com.google.inject.Injector
import dev.morphia.Datastore
import org.apache.logging.log4j.LogManager

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 18:12
 * @author Frederick Baier
 */
class NodePreparer(
    private val startArguments: NodeStartArgumentParserMain
) {

    private val restServer = RestServerAPI.createRestServer(NoAuthService(), 8008)
    private val restSetupManager = RestSetupManager(this.restServer)

    private val injector = createInjector()

    fun prepare(): Injector {
        logger.info("Starting Node...")
        val datastore = checkForMongoConnectionStringAndStartClient()
        val jwtTokenHandler = initJwtTokenHandler(datastore)
        val injector = createSubInjectorWithDatastoreAndTokenHandler(datastore, jwtTokenHandler)
        checkForAnyWebAccount(injector)
        setupEnd()
        logger.info("Node Startup completed")
        return injector
    }

    private fun checkForAnyWebAccount(injector: Injector) {
        injector.getInstance(FirstAccountCheck::class.java).checkForAccount()
    }

    private fun initJwtTokenHandler(datastore: Datastore): JwtTokenHandler {
        val tokenSecretRepo = MongoSingleObjectRepository(
            datastore,
            TokenSecretEntity::class.java,
            TokenSecretEntity.KEY
        )
        val entity = tokenSecretRepo.loadObject().join()
        return JwtTokenHandler(entity.secret)
    }

    private fun setupEnd() {
        this.restSetupManager.onEndOfAllSetups()
    }

    private fun createInjector(): Injector {
        return Guice.createInjector(
            KubernetesBinderModule(),
            SingleInstanceBinderModule(RestServer::class.java, this.restServer),
            SingleInstanceBinderModule(RestSetupManager::class.java, this.restSetupManager),
            SingleInstanceBinderModule(AuthServiceProvider::class.java, object: AuthServiceProvider {
                override fun getAuthService(): AuthService {
                    return restServer.getAuthService()
                }
            }),
        )
    }

    private fun createSubInjectorWithDatastoreAndTokenHandler(datastore: Datastore, tokenHandler: JwtTokenHandler): Injector {
        return this.injector.createChildInjector(
            SingleInstanceBinderModule(Datastore::class.java, datastore),
            SingleInstanceBinderModule(JwtTokenHandler::class.java, tokenHandler)
        )
    }

    private fun checkForMongoConnectionStringAndStartClient(): Datastore {
        return this.injector.getInstance(MongoClientSafeStarter::class.java).startMongoClient()
    }

    companion object {
        private val logger = LogManager.getLogger(NodePreparer::class.java)
    }

}