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

package app.simplecloud.simplecloud.restserver.setup

import app.simplecloud.simplecloud.restserver.base.RestServer
import app.simplecloud.simplecloud.restserver.base.RestServerAPI
import app.simplecloud.simplecloud.restserver.base.parameter.RequestBodyParameterType
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.base.route.Route
import app.simplecloud.simplecloud.restserver.base.vmethod.VirtualMethod
import app.simplecloud.simplecloud.restserver.setup.response.CurrentSetupRequestResponse
import app.simplecloud.simplecloud.restserver.setup.response.SetupEndResponse
import app.simplecloud.simplecloud.restserver.setup.type.Setup
import com.google.common.collect.Maps
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 05/08/2021
 * Time: 11:55
 * @author Frederick Baier
 */
class RestSetupManager(
    val restServer: RestServer
) {

    @Volatile
    var currentSetup: Setup<*>? = null
        private set

    private val setupNameToFuture = Maps.newConcurrentMap<String, CompletableFuture<Any>>()
    private val futuresWaitingForNextSetup = CopyOnWriteArrayList<CompletableFuture<Any>>()
    private val routes = CopyOnWriteArrayList<Route>()

    @Volatile
    private var endLoginToken: String = ""

    init {
        registerGetCurrentSetupRoute()
    }

    fun <T : Any> setNextSetup(setup: Setup<T>): CompletableFuture<T> {
        registerSetupRoute(setup)
        this.currentSetup = setup
        val future = CompletableFuture<T>()
        this.setupNameToFuture[setup.setupName] = future as CompletableFuture<Any>
        completeAllFuturesWaitingForNextSetup(CurrentSetupRequestResponse(setup))
        return future
    }

    private fun registerRoute(route: Route) {
        this.routes.add(route)
        this.restServer.registerRoute(route)
    }

    private fun completeAllFuturesWaitingForNextSetup(response: Any) {
        this.futuresWaitingForNextSetup.forEach { it.complete(response) }
    }

    private fun registerSetupRoute(setup: Setup<*>) {
        val methodBuilder = RestServerAPI.RouteMethodBuilderImpl()
        methodBuilder.addParameter(RequestBodyParameterType(emptyArray(), arrayOf(setup.responseClass.java)))
        methodBuilder.setVirtualMethod(createVirtualMethod(setup.setupName))
        val routeBuilder = RestServerAPI.RouteBuilderImpl()
        routeBuilder.setRequestType(RequestType.POST)
        routeBuilder.setPath("setup/${setup.setupName}")
        routeBuilder.setMethod(methodBuilder.build())
        registerRoute(routeBuilder.build())
    }

    private fun registerGetCurrentSetupRoute() {
        val methodBuilder = RestServerAPI.RouteMethodBuilderImpl()
        methodBuilder.setVirtualMethod(object : VirtualMethod {
            override fun invoke(vararg args: Any?): Any? {
                val currentSetup = this@RestSetupManager.currentSetup ?: return null
                return CurrentSetupRequestResponse(currentSetup)
            }
        })
        val routeBuilder = RestServerAPI.RouteBuilderImpl()
        routeBuilder.setRequestType(RequestType.GET)
        routeBuilder.setPath("setup")
        routeBuilder.setMethod(methodBuilder.build())
        registerRoute(routeBuilder.build())
    }

    private fun waitForNextSetupName(): CompletableFuture<CurrentSetupRequestResponse> {
        val future = CompletableFuture<CurrentSetupRequestResponse>()
        this.futuresWaitingForNextSetup.add(future as CompletableFuture<Any>)
        return future
    }

    fun setEndToken(token: String) {
        this.endLoginToken = token
    }

    fun onEndOfAllSetups() {
        unregisterAllRoutes()
        this.currentSetup = END_SETUP
        completeAllFuturesWaitingForNextSetup(SetupEndResponse(this.endLoginToken))
        this.endLoginToken = ""
    }

    private fun unregisterAllRoutes() {
        this.routes.forEach { this.restServer.unregisterRoute(it) }
    }

    private fun createVirtualMethod(setupName: String): VirtualMethod {
        return object : VirtualMethod {

            override fun invoke(vararg args: Any?): Any? {
                val future = waitForNextSetupName()
                this@RestSetupManager.setupNameToFuture[setupName]?.complete(args[0])
                return future.join()
            }

        }
    }

    companion object {
        private val END_SETUP = Setup("end", "", String::class)
    }

}