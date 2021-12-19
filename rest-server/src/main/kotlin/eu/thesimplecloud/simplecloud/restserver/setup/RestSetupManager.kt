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

package eu.thesimplecloud.simplecloud.restserver.setup

import com.google.common.collect.Maps
import eu.thesimplecloud.simplecloud.restserver.RestServer
import eu.thesimplecloud.simplecloud.restserver.annotation.RequestBody
import eu.thesimplecloud.simplecloud.restserver.annotation.RequestType
import eu.thesimplecloud.simplecloud.restserver.controller.Controller
import eu.thesimplecloud.simplecloud.restserver.controller.MethodRoute
import eu.thesimplecloud.simplecloud.restserver.controller.VirtualMethod
import eu.thesimplecloud.simplecloud.restserver.setup.response.CurrentSetupRequestResponse
import eu.thesimplecloud.simplecloud.restserver.setup.type.Setup
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.full.createInstance

/**
 * Created by IntelliJ IDEA.
 * Date: 05/08/2021
 * Time: 11:55
 * @author Frederick Baier
 */
class RestSetupManager(
    private val restServer: RestServer
) {

    @Volatile
    var currentSetup: Setup<*>? = null
        private set

    private val setupNameToFuture = Maps.newConcurrentMap<String, CompletableFuture<Any>>()
    private val futuresWaitingForNextSetup = CopyOnWriteArrayList<CompletableFuture<CurrentSetupRequestResponse>>()

    init {
        registerGetCurrentSetupRoute()
    }

    fun <T : Any> setNextSetup(setup: Setup<T>): CompletableFuture<T> {
        registerRoute(setup)
        this.currentSetup = setup
        val future = CompletableFuture<T>()
        this.setupNameToFuture[setup.setupName] = future as CompletableFuture<Any>
        completeAllFuturesWaitingForNextSetup(CurrentSetupRequestResponse(setup))
        return future
    }

    private fun completeAllFuturesWaitingForNextSetup(response: CurrentSetupRequestResponse) {
        this.futuresWaitingForNextSetup.forEach { it.complete(response) }
    }

    private fun registerRoute(setup: Setup<*>) {
        val requestBody = RequestBody::class.createInstance()
        val methodRouteParameter = MethodRoute.MethodRouteParameter(setup.responseClass.java, requestBody)
        val methodRoute = MethodRoute(
            RequestType.POST,
            "setup/${setup.setupName}",
            "",
            listOf(methodRouteParameter),
            createVirtualMethod(setup.setupName),
            object : Controller {}
        )

        this.restServer.registerMethodRoute(methodRoute)
    }

    private fun registerGetCurrentSetupRoute() {
        val virtualMethod = object : VirtualMethod {
            override fun invoke(invokeObj: Any, vararg args: Any?): Any? {
                val currentSetup = this@RestSetupManager.currentSetup ?: return null
                return CurrentSetupRequestResponse(currentSetup)
            }

        }
        val methodRoute = MethodRoute(
            RequestType.GET,
            "setup",
            "",
            emptyList(),
            virtualMethod,
            object : Controller {}
        )

        this.restServer.registerMethodRoute(methodRoute)
    }

    private fun waitForNextSetupName(): CompletableFuture<CurrentSetupRequestResponse> {
        val future = CompletableFuture<CurrentSetupRequestResponse>()
        this.futuresWaitingForNextSetup.add(future)
        return future
    }

    fun onEndOfAllSetups() {
        this.currentSetup = Setup.END
        completeAllFuturesWaitingForNextSetup(CurrentSetupRequestResponse(Setup.END))
    }

    private fun createVirtualMethod(setupName: String): VirtualMethod {
        return object : VirtualMethod {

            override fun invoke(invokeObj: Any, vararg args: Any?): Any {
                val future = waitForNextSetupName()
                this@RestSetupManager.setupNameToFuture[setupName]?.complete(args[0])
                return future.join()
            }

        }
    }

}