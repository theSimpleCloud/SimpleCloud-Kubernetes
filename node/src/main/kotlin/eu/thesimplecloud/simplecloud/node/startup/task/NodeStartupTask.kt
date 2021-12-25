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

package eu.thesimplecloud.simplecloud.node.startup.task

import com.ea.async.Async.await
import com.google.inject.Guice
import com.google.inject.Injector
import dev.morphia.Datastore
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.kubernetes.api.secret.KubeSecretService
import eu.thesimplecloud.simplecloud.kubernetes.impl.KubernetesBinderModule
import eu.thesimplecloud.simplecloud.node.startup.NodeStartArgumentParserMain
import eu.thesimplecloud.simplecloud.node.startup.NodeStartupSetupHandler
import eu.thesimplecloud.simplecloud.node.startup.guice.NodeBinderModule
import eu.thesimplecloud.simplecloud.node.startup.setup.task.FirstWebUserSetupTask
import eu.thesimplecloud.simplecloud.node.startup.task.mongo.MongoDbSafeStartTask
import eu.thesimplecloud.simplecloud.node.util.Logger
import eu.thesimplecloud.simplecloud.node.util.SingleInstanceBinderModule
import eu.thesimplecloud.simplecloud.restserver.repository.MongoUserRepository
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

    private val nodeSetupHandler: NodeStartupSetupHandler = NodeStartupSetupHandler()

    private val injector = createInjector()

    fun run(): CompletableFuture<Injector> {
        Logger.info("Starting Node...")
        val datastore = await(checkForMongoConnectionStringAndStartClient())
        await(checkForAnyWebAccount(datastore))
        this.nodeSetupHandler.shutdownRestSetupServer()
        Logger.info("Node Startup completed")
        val injector = createSubInjectorWithDatastore(datastore)
        return completedFuture(injector)
    }

    private fun createInjector(): Injector {
        return Guice.createInjector(
            KubernetesBinderModule(),
            SingleInstanceBinderModule(NodeStartupSetupHandler::class.java, this.nodeSetupHandler)
        )
    }

    private fun createSubInjectorWithDatastore(datastore: Datastore): Injector {
        return this.injector.createChildInjector(
            SingleInstanceBinderModule(Datastore::class.java, datastore)
        )
    }

    private fun checkForAnyWebAccount(datastore: Datastore): CompletableFuture<Unit> {
        val mongoRepository = MongoUserRepository(datastore)
        val count = await(mongoRepository.count())
        if (count == 0L) {
            return executeFirstUserSetup(mongoRepository)
        }
        return unitFuture()
    }

    private fun executeFirstUserSetup(mongoRepository: MongoUserRepository): CompletableFuture<Unit> {
        return this.nodeSetupHandler.executeSetupTask() {
            FirstWebUserSetupTask(it, mongoRepository).run()
        }
    }

    private fun checkForMongoConnectionStringAndStartClient(): CompletableFuture<Datastore> {
        return this.injector.getInstance(MongoDbSafeStartTask::class.java).run()
    }


}