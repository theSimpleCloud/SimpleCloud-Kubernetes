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

package app.simplecloud.simplecloud.node.resourcedefinition.web

import app.simplecloud.simplecloud.module.api.internal.service.InternalLinkService
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.module.api.service.ResourceDefinitionService
import app.simplecloud.simplecloud.restserver.api.RestServer
import app.simplecloud.simplecloud.restserver.api.route.RequestType
import app.simplecloud.simplecloud.restserver.api.route.Route
import app.simplecloud.simplecloud.restserver.api.vmethod.VirtualMethod

/**
 * Date: 15.01.23
 * Time: 12:07
 * @author Frederick Baier
 *
 */
class ResourceDefinitionRouteRegisterer(
    private val restServer: RestServer,
    private val resourceDefinitionService: ResourceDefinitionService,
    private val resourceRequestHandler: ResourceRequestHandler,
    private val linkService: InternalLinkService,
) {

    private val webRequestHandler = WebRequestHandler(resourceDefinitionService, resourceRequestHandler, linkService)

    fun registerRoutes() {
        restServer.registerRoute(createCreateRoute())
        restServer.registerRoute(createDeleteRoute())
        restServer.registerRoute(createUpdateRoute())
        restServer.registerRoute(createGetOneRoute())
        restServer.registerRoute(createGetAllRoute())

        restServer.registerRoute(createCustomActionRoute())

        restServer.registerRoute(createCreateLinkRoute())
        restServer.registerRoute(createGetLinkDefinitionsRoute())
        restServer.registerRoute(createGetLinksByTypeRoute())
        restServer.registerRoute(createDeleteLinkRoute())
    }

    private fun createDeleteLinkRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.DELETE)
        routeBuilder.setPath("api/link/{type}/{oneResourceName}")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleDeleteLink",
                    String::class.java,
                    String::class.java
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeMethodBuilder.addPathParameterType("type")
        routeMethodBuilder.addPathParameterType("oneResourceName")
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    private fun createGetLinksByTypeRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.GET)
        routeBuilder.setPath("api/link/{type}")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleGetLinksByType",
                    String::class.java
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeMethodBuilder.addPathParameterType("type")
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    private fun createGetLinkDefinitionsRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.GET)
        routeBuilder.setPath("api/link")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleGetLinkDefinitions",
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    private fun createCreateLinkRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.POST)
        routeBuilder.setPath("api/link")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleLinkCreate",
                    String::class.java,
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeMethodBuilder.addBodyParameterType()
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    private fun createCustomActionRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.POST)
        routeBuilder.setPath("api/custom/{group}/{version}/{kind}/{name}/{action}")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleCustomAction",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeMethodBuilder.addPathParameterType("group")
        routeMethodBuilder.addPathParameterType("version")
        routeMethodBuilder.addPathParameterType("kind")
        routeMethodBuilder.addPathParameterType("name")
        routeMethodBuilder.addPathParameterType("action")
        routeMethodBuilder.addBodyParameterType()
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    private fun createDeleteRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.DELETE)
        routeBuilder.setPath("api/custom/{group}/{version}/{kind}/{name}")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleDeleteOne",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeMethodBuilder.addPathParameterType("group")
        routeMethodBuilder.addPathParameterType("version")
        routeMethodBuilder.addPathParameterType("kind")
        routeMethodBuilder.addPathParameterType("name")
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    private fun createGetOneRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.GET)
        routeBuilder.setPath("api/custom/{group}/{version}/{kind}/{name}")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleGetOne",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeMethodBuilder.addPathParameterType("group")
        routeMethodBuilder.addPathParameterType("version")
        routeMethodBuilder.addPathParameterType("kind")
        routeMethodBuilder.addPathParameterType("name")
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    private fun createGetAllRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.GET)
        routeBuilder.setPath("api/custom/{group}/{version}/{kind}")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleGetAll",
                    String::class.java,
                    String::class.java,
                    String::class.java
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeMethodBuilder.addPathParameterType("group")
        routeMethodBuilder.addPathParameterType("version")
        routeMethodBuilder.addPathParameterType("kind")
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    private fun createUpdateRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.PUT)
        routeBuilder.setPath("api/custom")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleUpdate",
                    String::class.java
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeMethodBuilder.addBodyParameterType()
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    private fun createCreateRoute(): Route {
        val routeBuilder = restServer.newRouteBuilder()
        routeBuilder.setRequestType(RequestType.POST)
        routeBuilder.setPath("api/custom")
        val routeMethodBuilder = routeBuilder.newRouteMethodBuilder()
        routeMethodBuilder.setVirtualMethod(
            VirtualMethod.fromRealMethod(
                this@ResourceDefinitionRouteRegisterer::class.java.getDeclaredMethod(
                    "handleCreate",
                    String::class.java
                ),
                this@ResourceDefinitionRouteRegisterer
            )
        )
        routeMethodBuilder.addBodyParameterType()
        routeBuilder.setMethod(routeMethodBuilder.build())
        return routeBuilder.build()
    }

    //the methods below are called using reflections and are registered above
    fun handleCreate(body: String): Boolean {
        return this.webRequestHandler.handleCreate(body)
    }

    fun handleUpdate(body: String): Boolean {
        return this.webRequestHandler.handleUpdate(body)
    }

    fun handleGetOne(group: String, version: String, kind: String, name: String): Any {
        return this.webRequestHandler.handleGetOne(group, version, kind, name)
    }

    fun handleGetAll(group: String, version: String, kind: String): Any {
        return this.webRequestHandler.handleGetAll(group, version, kind)
    }

    fun handleDeleteOne(group: String, version: String, kind: String, name: String): Any {
        return this.webRequestHandler.handleDeleteOne(group, version, kind, name)
    }

    fun handleCustomAction(
        group: String,
        version: String,
        kind: String,
        name: String,
        action: String,
        body: String,
    ): Any {
        return this.webRequestHandler.handleCustomAction(group, version, kind, name, action, body)
    }

    fun handleLinkCreate(
        body: String,
    ): Any {
        return this.webRequestHandler.handleLinkCreate(body)
    }

    fun handleGetLinkDefinitions(): Any {
        return this.webRequestHandler.handleGetLinkDefinitions()
    }

    fun handleGetLinksByType(type: String): Any {
        return this.webRequestHandler.handleGetLinksByType(type)
    }

    fun handleDeleteLink(type: String, oneResourceName: String): Any {
        return this.webRequestHandler.handleDeleteLink(type, oneResourceName)
    }

}