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

package app.simplecloud.simplecloud.restserver

import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.*
import com.ea.async.Async
import com.google.inject.Guice
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 16/07/2021
 * Time: 21:24
 * @author Frederick Baier
 */
class Test {




}

fun main() {
    Async.init()
    val injector = Guice.createInjector(TestRestBinderModule())
    println(File(".").absolutePath)
    val restServer = injector.getInstance(RestServer::class.java)
    val controllerHandler = restServer.controllerHandler
    controllerHandler.registerController(UserController::class.java)
    controllerHandler.registerController(LoginController::class.java)
    controllerHandler.registerController(ProcessGroupController::class.java)
    controllerHandler.registerController(ProcessController::class.java)
    controllerHandler.registerController(NodeController::class.java)
    while (true) {

    }
}
