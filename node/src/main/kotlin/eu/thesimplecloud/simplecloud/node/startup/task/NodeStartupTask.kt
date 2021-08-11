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
import eu.thesimplecloud.simplecloud.api.future.voidFuture
import eu.thesimplecloud.simplecloud.node.startup.NodeStartArgumentParserMain
import eu.thesimplecloud.simplecloud.node.startup.NodeStartupSetupHandler
import eu.thesimplecloud.simplecloud.node.startup.setup.task.FirstWebUserSetupTask
import eu.thesimplecloud.simplecloud.node.startup.task.docker.DockerInstallTask
import eu.thesimplecloud.simplecloud.node.startup.task.docker.DockerSafeInstallTask
import eu.thesimplecloud.simplecloud.node.startup.task.docker.EnsureInsideDockerAndDockerIsAccessibleTask
import eu.thesimplecloud.simplecloud.node.startup.task.mongo.MongoDbSafeStartTask
import eu.thesimplecloud.simplecloud.restserver.repository.MongoUserRepository
import eu.thesimplecloud.simplecloud.task.Task
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 18:12
 * @author Frederick Baier
 */
class NodeStartupTask(
    private val startArguments: NodeStartArgumentParserMain
) : Task<Void>() {

    private val nodeSetupHandler: NodeStartupSetupHandler = NodeStartupSetupHandler()

    override fun getName(): String {
        return "node_startup"
    }

    override fun run(): CompletableFuture<Void> {
        await(this.taskSubmitter.submit(EnsureInsideDockerAndDockerIsAccessibleTask()))
        val datastore = await(checkForMongoConnectionStringAndStartClient())
        await(checkForAnyWebAccount(datastore))
        val injector = await(loadModulesAndCreateGuiceInjector(datastore))
        this.nodeSetupHandler.shutdownRestSetupServer()
        return voidFuture()
    }

    private fun loadModulesAndCreateGuiceInjector(datastore: Datastore): CompletableFuture<Injector> {
        val loadedModules = await(this.taskSubmitter.submit(LoadModulesTask(datastore, nodeSetupHandler)))
        val guiceModules = loadedModules.map { it.getLoadedClassInstance() }
        return completedFuture(Guice.createInjector(guiceModules))
    }

    private fun checkForAnyWebAccount(datastore: Datastore): CompletableFuture<Void> {
        val mongoRepository = MongoUserRepository(datastore)
        val count = await(mongoRepository.count())
        if (count == 0L) {
            await(this.nodeSetupHandler.executeSetupTask(this.taskSubmitter) {
                FirstWebUserSetupTask(it, mongoRepository)
            })
        }
        return voidFuture()
    }

    private fun checkForMongoConnectionStringAndStartClient(): CompletableFuture<Datastore> {
        return this.taskSubmitter.submit(MongoDbSafeStartTask(this.startArguments.mongoDbConnectionString, this.nodeSetupHandler))
    }


}