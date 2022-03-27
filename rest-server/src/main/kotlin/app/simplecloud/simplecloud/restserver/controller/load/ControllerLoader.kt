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

package app.simplecloud.simplecloud.restserver.controller.load

import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.restserver.annotation.*
import app.simplecloud.simplecloud.restserver.base.RestServerAPI
import app.simplecloud.simplecloud.restserver.base.parameter.ParameterType
import app.simplecloud.simplecloud.restserver.base.parameter.PathParamParameterType
import app.simplecloud.simplecloud.restserver.base.parameter.RequestBodyParameterType
import app.simplecloud.simplecloud.restserver.base.parameter.RequestingEntityParameterType
import app.simplecloud.simplecloud.restserver.base.route.Route
import app.simplecloud.simplecloud.restserver.base.route.RouteMethod
import app.simplecloud.simplecloud.restserver.base.vmethod.VirtualMethod
import app.simplecloud.simplecloud.restserver.controller.Controller
import java.lang.reflect.Method
import java.lang.reflect.Parameter

/**
 * Created by IntelliJ IDEA.
 * Date: 23.06.2021
 * Time: 09:42
 * @author Frederick Baier
 */
class ControllerLoader(
    private val controller: Controller
) {

    private val controllerClass = this.controller::class.java
    private val controllerAnnotation = controllerClass.getAnnotation(RestController::class.java)

    fun generateRoutes(): List<Route> {
        checkForControllerAnnotation()
        return generateRoutes0()
    }

    private fun generateRoutes0(): List<Route> {
        val methods = getMethodsToGenerateRoutesFor()
        return methods.map { createRoute(it) }
    }

    private fun createRoute(method: Method): Route {
        val routeMethod = buildRouteMethod(method)
        val requestMappingAnnotation = method.getAnnotation(RequestMapping::class.java)
        val routeBuilder = RestServerAPI.RouteBuilderImpl()
        routeBuilder.setPath(generatePath(requestMappingAnnotation))
        routeBuilder.setPermission(requestMappingAnnotation.permission)
        routeBuilder.setRequestType(requestMappingAnnotation.requestType)
        routeBuilder.setMethod(routeMethod)
        return routeBuilder.build()
    }

    private fun buildRouteMethod(method: Method): RouteMethod {
        val parameters = method.parameters.map { createParameter(it) }
        val routeMethodBuilder = RestServerAPI.RouteMethodBuilderImpl()
        routeMethodBuilder.setVirtualMethod(VirtualMethod.fromRealMethod(method, controller))
        parameters.forEach { routeMethodBuilder.addParameter(it) }
        return routeMethodBuilder.build()
    }

    private fun generatePath(requestMappingAnnotation: RequestMapping): String {
        val requestPath = requestMappingAnnotation.additionalPath
        val topic = controllerAnnotation.topic
        val controllerPath = controllerAnnotation.additionalPath
        val version = controllerAnnotation.version
        val listOfPaths = listOf<String>(API_PREFIX, "v${version}", topic, controllerPath, requestPath)
            .filter { it.isNotEmpty() }
        return listOfPaths.joinToString("/")
    }

    private fun includeSlashAfterPathAndAdditionalIfPresent(path: String, additionalPath: String): String {
        if (path.isEmpty()) return ""
        if (additionalPath.isEmpty()) return path
        return "$path/"
    }

    private fun createParameter(parameter: Parameter): ParameterType {
        if (isParameterRequestingEntity(parameter)) {
            return RequestingEntityParameterType()
        }
        if (parameter.isAnnotationPresent(RequestPathParam::class.java))
            //TODO add assertion for string type
            return PathParamParameterType(parameter.getAnnotation(RequestPathParam::class.java).parameterName)

        if (parameter.isAnnotationPresent(RequestBody::class.java)) {
            val requestBody = parameter.getAnnotation(RequestBody::class.java)
            val classes = if (requestBody.classes.isEmpty())
                listOf(parameter.type)
            else
                requestBody.classes.map { it.java }
            return RequestBodyParameterType(requestBody.types, classes.toTypedArray())
        }

        throw IllegalArgumentException("Parameter with type ${parameter.type.name} in not annotated with RequestBody, RequestParam or RequestPathParam")
    }

    private fun isParameterRequestingEntity(parameter: Parameter): Boolean {
        return parameter.type == PermissionEntity::class.java && parameter.isAnnotationPresent(RequestingEntity::class.java)
    }


    private fun getMethodsToGenerateRoutesFor(): List<Method> {
        return this.controllerClass.methods.filter { it.isAnnotationPresent(RequestMapping::class.java) }
    }

    private fun checkForControllerAnnotation() {
        check(controllerAnnotation != null)
    }

    companion object {
        const val API_PREFIX = "api"
    }

}