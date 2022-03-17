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

package app.simplecloud.simplecloud.restserver.controller.load

import app.simplecloud.simplecloud.restserver.annotation.*
import app.simplecloud.simplecloud.restserver.base.RestServerAPI
import app.simplecloud.simplecloud.restserver.base.route.Route
import app.simplecloud.simplecloud.restserver.base.vmethod.VirtualMethod
import app.simplecloud.simplecloud.restserver.controller.Controller
import app.simplecloud.simplecloud.restserver.controller.MethodRoute
import app.simplecloud.simplecloud.restserver.controller.VirtualMethod
import app.simplecloud.simplecloud.restserver.user.User
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
        return methods.map { createMethodRoute(it) }
    }

    private fun createMethodRoute(method: Method): Route {
        val requestMappingAnnotation = method.getAnnotation(RequestMapping::class.java)
        val parameters = method.parameters.map { createParameter(it) }
        RestServerAPI.RouteMethodBuilderImpl()
            .setVirtualMethod(VirtualMethod.fromRealMethod(method, controller))
            .addParameter()
        return MethodRoute(
            requestMappingAnnotation.requestType,
            generatePath(requestMappingAnnotation),
            requestMappingAnnotation.permission,
            parameters,
            VirtualMethod.fromRealMethod(method),
            controller
        )
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

    private fun createParameter(parameter: Parameter): MethodRoute.MethodRouteParameter {
        if (isParameterRequestingUser(parameter)) {
            return createRequestingUserParameter(parameter)
        }
        if (parameter.isAnnotationPresent(RequestPathParam::class.java))
            return MethodRoute.MethodRouteParameter(parameter.type, parameter.getAnnotation(RequestPathParam::class.java))

        if (parameter.isAnnotationPresent(RequestBody::class.java))
            return MethodRoute.MethodRouteParameter(parameter.type, parameter.getAnnotation(RequestBody::class.java))

        throw IllegalArgumentException("Parameter with type ${parameter.type.name} in not annotated with RequestBody, RequestParam or RequestPathParam")
    }

    private fun isParameterRequestingUser(parameter: Parameter) =
        parameter.type == User::class.java && parameter.isAnnotationPresent(RequestingUser::class.java)

    private fun createRequestingUserParameter(parameter: Parameter): MethodRoute.MethodRouteParameter {
        val requestingUser = parameter.getAnnotation(RequestingUser::class.java)
        return MethodRoute.MethodRouteParameter(User::class.java, requestingUser)
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