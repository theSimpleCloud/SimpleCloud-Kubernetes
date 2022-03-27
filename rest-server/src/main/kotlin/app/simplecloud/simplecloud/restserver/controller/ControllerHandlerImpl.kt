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

package app.simplecloud.simplecloud.restserver.controller

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

    override fun getRestServer(): RestServer {
        return this.restServer
    }
}