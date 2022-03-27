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

package app.simplecloud.simplecloud.restserver.base.impl

import app.simplecloud.rest.Context
import app.simplecloud.rest.Handler
import app.simplecloud.rest.RequestMethod
import app.simplecloud.rest.RestServerFactory
import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.restserver.base.RestServer
import app.simplecloud.simplecloud.restserver.base.exclude.annotation.WebExcludeAll
import app.simplecloud.simplecloud.restserver.base.exclude.annotation.WebExcludeIncoming
import app.simplecloud.simplecloud.restserver.base.exclude.annotation.WebExcludeOutgoing
import app.simplecloud.simplecloud.restserver.base.exclude.introspector.AnnotationExcludeIntrospector
import app.simplecloud.simplecloud.restserver.base.route.Route
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Created by IntelliJ IDEA.
 * Date: 22.06.2021
 * Time: 16:57
 * @author Frederick Baier
 */
class RestServerBase(
    @Volatile
    private var authService: AuthService,
    port: Int
) : RestServer {

    private val server = RestServerFactory.createServer(port)

    private val registeredRoutes = CopyOnWriteArrayList<RegisteredRoute>()

    init {
        this.server.start()
    }

    override fun getAuthService(): AuthService {
        return this.authService
    }

    override fun setAuthService(authService: AuthService) {
        this.authService = authService
    }

    override fun registerRoute(route: Route) {
        val lowRoute = this.server.registerRoute(
            RequestMethod.valueOf(route.getRequestType().name),
            route.getPath(),
            object : Handler {
                override fun handle(context: Context) {
                    CloudScope.launch {
                        handleIncomingRequest(route, context)
                        context.flush()
                    }
                }
            }
        )
        this.registeredRoutes.add(RegisteredRoute(route, lowRoute))
    }

    override fun unregisterRoute(route: Route) {
        val lowRoute = this.registeredRoutes.firstOrNull { it.highRoute == route }?.lowRoute
        lowRoute?.let { this.server.unregisterRoute(it) }
    }

    private suspend fun handleIncomingRequest(route: Route, context: Context) {
        val requestBody = context.getRequestBody()
        val request = RequestCreator(route, context, requestBody, this.authService).createRequest()
        ResponseHandler(context, route, request).handle()
    }

    override fun stop() {
        this.server.stop()
    }

    class RegisteredRoute(
        val highRoute: Route,
        val lowRoute: app.simplecloud.rest.Route
    )

    companion object {

        val mapperExcludeOutgoing = ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setAnnotationIntrospector(
                AnnotationExcludeIntrospector(
                    WebExcludeOutgoing::class.java,
                    WebExcludeAll::class.java
                )
            )

        val mapperExcludeIncoming = ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setAnnotationIntrospector(
                AnnotationExcludeIntrospector(
                    WebExcludeIncoming::class.java,
                    WebExcludeAll::class.java
                )
            )

    }

}