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

package app.simplecloud.simplecloud.node.startup.prepare

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
        this.controllerHandler.registerController(OnlineStrategyController::class.java)
    }


}