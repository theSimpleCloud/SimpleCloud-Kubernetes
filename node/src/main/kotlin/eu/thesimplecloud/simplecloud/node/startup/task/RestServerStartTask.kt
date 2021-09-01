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

import com.google.inject.Guice
import com.google.inject.Injector
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import eu.thesimplecloud.simplecloud.restserver.RestBinderModule
import eu.thesimplecloud.simplecloud.restserver.RestServer
import eu.thesimplecloud.simplecloud.restserver.controller.ControllerHandler
import eu.thesimplecloud.simplecloud.restserver.defaultcontroller.v1.*
import eu.thesimplecloud.simplecloud.restserver.setup.SetupRestServerBinderModule
import eu.thesimplecloud.simplecloud.task.Task
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitter
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 11:22
 * @author Frederick Baier
 */
class RestServerStartTask(
    private val injector: Injector
) : Task<RestServer>() {

    override fun getName(): String {
        return "start_rest_server"
    }

    override fun run(): CompletableFuture<RestServer> {
        val injector = initGuice()
        val restServer = injector.getInstance(RestServer::class.java)
        registerController(restServer.controllerHandler)
        return CloudCompletableFuture.completedFuture(restServer)
    }

    private fun registerController(controllerHandler: ControllerHandler) {
        controllerHandler.registerController(UserController::class.java)
        controllerHandler.registerController(LoginController::class.java)
        controllerHandler.registerController(ProcessGroupController::class.java)
        controllerHandler.registerController(ProcessController::class.java)
        controllerHandler.registerController(TemplateController::class.java)
        controllerHandler.registerController(JvmArgumentsController::class.java)
        controllerHandler.registerController(ProcessVersionController::class.java)
        controllerHandler.registerController(NodeController::class.java)
    }

    private fun initGuice(): Injector {
        return this.injector.createChildInjector(RestBinderModule())
    }


}