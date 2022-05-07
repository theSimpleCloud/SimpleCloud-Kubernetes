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

package app.simplecloud.simplecloud.restserver.impl.setup

import app.simplecloud.simplecloud.restserver.api.RestServer
import app.simplecloud.simplecloud.restserver.api.route.RequestType
import app.simplecloud.simplecloud.restserver.api.route.Route
import app.simplecloud.simplecloud.restserver.api.setup.RestSetupManager
import app.simplecloud.simplecloud.restserver.api.setup.Setup
import app.simplecloud.simplecloud.restserver.api.vmethod.VirtualMethod
import app.simplecloud.simplecloud.restserver.base.parameter.RequestBodyParameterType
import app.simplecloud.simplecloud.restserver.impl.setup.response.CurrentSetupRequestResponse
import app.simplecloud.simplecloud.restserver.impl.setup.response.SetupEndResponse
import com.google.common.collect.Maps
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 05/08/2021
 * Time: 11:55
 * @author Frederick Baier
 */
class RestSetupManagerImpl(
    private val restServer: RestServer
) : RestSetupManager {

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

    override fun <T : Any> setNextSetup(setup: Setup<T>): CompletableFuture<T> {
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
        val routeBuilder = this.restServer.newRouteBuilder()
        val methodBuilder = routeBuilder.newRouteMethodBuilder()
        methodBuilder.addParameter(RequestBodyParameterType(emptyArray(), arrayOf(setup.responseClass.java)))
        methodBuilder.setVirtualMethod(createVirtualMethod(setup.setupName))
        routeBuilder.setRequestType(RequestType.POST)
        routeBuilder.setPath("setup/${setup.setupName}")
        routeBuilder.setMethod(methodBuilder.build())
        registerRoute(routeBuilder.build())
    }

    private fun registerGetCurrentSetupRoute() {
        val routeBuilder = this.restServer.newRouteBuilder()
        val methodBuilder = routeBuilder.newRouteMethodBuilder()
        methodBuilder.setVirtualMethod(object : VirtualMethod {
            override fun invoke(vararg args: Any?): Any? {
                val currentSetup = this@RestSetupManagerImpl.currentSetup ?: return null
                return CurrentSetupRequestResponse(currentSetup)
            }
        })
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

    override fun setEndToken(token: String) {
        this.endLoginToken = token
    }

    override fun onEndOfAllSetups() {
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
                this@RestSetupManagerImpl.setupNameToFuture[setupName]?.complete(args[0])
                return future.join()
            }

        }
    }

    companion object {
        private val END_SETUP = Setup("end", "", String::class)
    }

}