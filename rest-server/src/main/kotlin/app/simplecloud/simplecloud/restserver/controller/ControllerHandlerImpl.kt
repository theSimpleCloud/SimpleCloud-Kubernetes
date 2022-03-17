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

package app.simplecloud.simplecloud.restserver.controller

import app.simplecloud.simplecloud.restserver.RestServer
import app.simplecloud.simplecloud.restserver.base.RestServer
import app.simplecloud.simplecloud.restserver.controller.load.ControllerLoader
import com.google.inject.Injector


/**
 * Created by IntelliJ IDEA.
 * Date: 23.06.2021
 * Time: 09:39
 * @author Frederick Baier
 */
class ControllerHandlerImpl constructor(
    private val restServer: RestServer,
    private val injector: Injector
) : ControllerHandler {

    override fun registerController(controllerClass: Class<out Controller>) {
        val routes = ControllerLoader(injector.getInstance(controllerClass)).generateRoutes()
        routes.forEach { this.restServer.registerRoute(it) }
    }

    override fun unregisterController(controllerClass: Class<out Controller>) {
        TODO("Not yet implemented")
    }


}