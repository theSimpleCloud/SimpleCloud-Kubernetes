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

package eu.thesimplecloud.simplecloud.restserver.controller

import eu.thesimplecloud.simplecloud.restserver.annotation.RequestType
import java.lang.reflect.InvocationTargetException

/**
 * Created by IntelliJ IDEA.
 * Date: 23.06.2021
 * Time: 09:42
 * @author Frederick Baier
 */
data class MethodRoute(
    val requestType: RequestType,
    val path: String,
    val permission: String,
    val parameters: List<MethodRouteParameter>,
    private val virtualMethod: VirtualMethod,
    private val controller: Controller
) {

    fun invokeMethodWithArgs(args: List<Any?>): Any? {
        try {
            return virtualMethod.invoke(controller, *args.toTypedArray())
        } catch (ex: Exception) {
            rethrowWithoutInvocationTargetException(ex)
        }
    }

    private fun rethrowWithoutInvocationTargetException(ex: Exception): Nothing {
        if (ex is InvocationTargetException) {
            throw ex.cause ?: ex
        }
        throw ex
    }

    class MethodRouteParameter(
        val parameterType: Class<*>,
        val annotation: Annotation?
    )

}