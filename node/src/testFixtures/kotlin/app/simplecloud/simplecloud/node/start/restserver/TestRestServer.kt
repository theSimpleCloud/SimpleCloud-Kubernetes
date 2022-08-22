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

package app.simplecloud.simplecloud.node.start.restserver

import app.simplecloud.simplecloud.restserver.api.RestServer
import app.simplecloud.simplecloud.restserver.api.auth.AuthService
import app.simplecloud.simplecloud.restserver.api.route.Route
import app.simplecloud.simplecloud.restserver.api.route.RouteBuilder
import app.simplecloud.simplecloud.restserver.base.request.RouteBuilderImpl

/**
 * Date: 11.05.22
 * Time: 10:01
 * @author Frederick Baier
 *
 */
class TestRestServer(
    @Volatile
    private var authService: AuthService
) : RestServer {

    override fun setAuthService(authService: AuthService) {
        this.authService = authService
    }

    override fun getAuthService(): AuthService {
        return authService
    }

    override fun registerRoute(route: Route) {

    }

    override fun unregisterRoute(route: Route) {

    }

    override fun stop() {

    }

    override fun newRouteBuilder(): RouteBuilder {
        return RouteBuilderImpl()
    }
}