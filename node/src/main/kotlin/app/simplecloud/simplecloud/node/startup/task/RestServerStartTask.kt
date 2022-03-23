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

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.restserver.auth.RestAuthServiceImpl
import app.simplecloud.simplecloud.restserver.base.RestServer
import app.simplecloud.simplecloud.restserver.controller.ControllerHandlerImpl
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.*
import com.google.inject.Inject
import com.google.inject.Injector
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 11:22
 * @author Frederick Baier
 */
class RestServerStartTask @Inject constructor(
    private val authService: RestAuthServiceImpl,
    private val restServer: RestServer,
    private val injector: Injector
) {

    private val controllerHandler = ControllerHandlerImpl(this.restServer, this.injector)

    init {
        this.restServer.setAuthService(authService)
    }

    fun run(): CompletableFuture<RestServer> {
        registerController()
        return CloudCompletableFuture.completedFuture(restServer)
    }

    private fun registerController() {
        this.controllerHandler.registerController(LoginController::class.java)
        this.controllerHandler.registerController(ProcessGroupController::class.java)
        this.controllerHandler.registerController(ProcessController::class.java)
        this.controllerHandler.registerController(NodeController::class.java)
        this.controllerHandler.registerController(PermissionGroupController::class.java)
        this.controllerHandler.registerController(PlayerController::class.java)
    }


}