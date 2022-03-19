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

package app.simplecloud.simplecloud.node.startup.task

import app.simplecloud.simplecloud.api.future.completedFuture
import app.simplecloud.simplecloud.api.future.unitFuture
import app.simplecloud.simplecloud.api.impl.util.SingleInstanceBinderModule
import app.simplecloud.simplecloud.kubernetes.impl.KubernetesBinderModule
import app.simplecloud.simplecloud.node.mongo.MongoSingleObjectRepository
import app.simplecloud.simplecloud.node.mongo.player.MongoCloudPlayerRepository
import app.simplecloud.simplecloud.node.startup.NodeStartArgumentParserMain
import app.simplecloud.simplecloud.node.startup.setup.task.FirstWebUserSetupTask
import app.simplecloud.simplecloud.node.startup.task.mongo.MongoDbSafeStartTask
import app.simplecloud.simplecloud.node.startup.token.TokenSecretEntity
import app.simplecloud.simplecloud.restserver.auth.AuthServiceProvider
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.base.RestServer
import app.simplecloud.simplecloud.restserver.base.RestServerAPI
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import app.simplecloud.simplecloud.restserver.base.service.NoAuthService
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import com.ea.async.Async.await
import com.google.inject.Guice
import com.google.inject.Injector
import dev.morphia.Datastore
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 18:12
 * @author Frederick Baier
 */
class NodeStartupTask(
    private val startArguments: NodeStartArgumentParserMain
) {

    private val restServer = RestServerAPI.createRestServer(NoAuthService(), 8008)
    private val restSetupManager = RestSetupManager(this.restServer)
    //TODO change secret

    private val injector = createInjector()

    fun run(): CompletableFuture<Injector> {
        logger.info("Starting Node...")
        val datastore = await(checkForMongoConnectionStringAndStartClient())
        val jwtTokenHandler = await(initJwtTokenHandler(datastore))
        val injector = createSubInjectorWithDatastoreAndTokenHandler(datastore, jwtTokenHandler)
        await(checkForAnyWebAccount(injector, jwtTokenHandler))
        setupEnd()
        logger.info("Node Startup completed")
        return completedFuture(injector)
    }

    private fun initJwtTokenHandler(datastore: Datastore): CompletableFuture<JwtTokenHandler> {
        val tokenSecretRepo = MongoSingleObjectRepository(
            datastore,
            TokenSecretEntity::class.java,
            TokenSecretEntity.KEY
        )
        val entity = await(tokenSecretRepo.loadObject())
        return completedFuture(JwtTokenHandler(entity.secret))
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

    private fun checkForAnyWebAccount(injector: Injector, tokenHandler: JwtTokenHandler): CompletableFuture<Unit> {
        val mongoRepository = injector.getInstance(MongoCloudPlayerRepository::class.java)
        val count = await(mongoRepository.count())
        if (count == 0L) {
            return executeFirstUserSetup(mongoRepository, tokenHandler)
        }
        return unitFuture()
    }

    private fun executeFirstUserSetup(mongoRepository: MongoCloudPlayerRepository, tokenHandler: JwtTokenHandler): CompletableFuture<Unit> {
        return FirstWebUserSetupTask(this.restSetupManager, mongoRepository, tokenHandler).run()
    }

    private fun checkForMongoConnectionStringAndStartClient(): CompletableFuture<Datastore> {
        return this.injector.getInstance(MongoDbSafeStartTask::class.java).run()
    }

    companion object {
        private val logger = LogManager.getLogger(NodeStartupTask::class.java)
    }

}