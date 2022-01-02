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

package app.simplecloud.simplecloud.node.startup

import app.simplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import app.simplecloud.simplecloud.node.startup.task.SetupRestServerStartTask
import app.simplecloud.simplecloud.restserver.RestServer
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import com.ea.async.Async.await
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07/08/2021
 * Time: 11:37
 * @author Frederick Baier
 */
class NodeStartupSetupHandler() {

    @Volatile
    private var setupRestServer: RestServer? = null

    @Volatile
    private var restSetupManager: RestSetupManager? = null

    fun <T> executeSetupTask(function: (RestSetupManager) -> CompletableFuture<T>): CompletableFuture<T> {
        val restSetupManager = await(getRestSetupManager())
        return function(restSetupManager)
    }

    private fun getRestSetupManager(): CompletableFuture<RestSetupManager> {
        if (this.restSetupManager != null) return CloudCompletableFuture.completedFuture(this.restSetupManager!!)
        val restServer = await(startRestSetupServerIfNeeded())
        this.restSetupManager = RestSetupManager(restServer)
        return CloudCompletableFuture.completedFuture(this.restSetupManager!!)
    }

    private fun startRestSetupServerIfNeeded(): CompletableFuture<RestServer> {
        if (this.setupRestServer != null) return CloudCompletableFuture.completedFuture(this.setupRestServer!!)
        val restServer = SetupRestServerStartTask().run()
        this.setupRestServer = restServer
        return CloudCompletableFuture.completedFuture(this.setupRestServer!!)
    }

    fun shutdownRestSetupServer() {
        this.restSetupManager?.onEndOfAllSetups()
        this.setupRestServer?.shutdown()
    }

}