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

package eu.thesimplecloud.simplecloud.restserver.defaultcontroller.v1

import com.ea.async.Async.await
import com.google.inject.Inject
import eu.thesimplecloud.simplecloud.api.jvmargs.configuration.JvmArgumentConfiguration
import eu.thesimplecloud.simplecloud.api.service.JvmArgumentsService
import eu.thesimplecloud.simplecloud.restserver.annotation.*
import eu.thesimplecloud.simplecloud.restserver.controller.Controller

/**
 * Created by IntelliJ IDEA.
 * Date: 11/07/2021
 * Time: 22:10
 * @author Frederick Baier
 */
@RestController(1, "cloud/jvmargs")
class JvmArgumentsController @Inject constructor(
    private val jvmService: JvmArgumentsService,
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.jvmargs.get")
    fun handleGetAll(): List<JvmArgumentConfiguration> {
        val list = await(this.jvmService.findAll())
        return list.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.jvmargs.get")
    fun handleGetOne(@RequestPathParam("name") name: String): JvmArgumentConfiguration {
        val jvmArguments = await(this.jvmService.findByName(name))
        return jvmArguments.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.jvmargs.create")
    fun handlePost(@RequestBody configuration: JvmArgumentConfiguration): Boolean {
        val request = this.jvmService.createJvmArgumentsCreateRequest(configuration)
        await(request.submit())
        return true
    }

    @RequestMapping(RequestType.DELETE, "{name}", "web.cloud.jvmargs.delete")
    fun handleDelete(@RequestPathParam("name") name: String): Boolean {
        val jvmArgs = await(this.jvmService.findByName(name))
        val request = this.jvmService.createJvmArgumentsDeleteRequest(jvmArgs)
        await(request.submit())
        return true
    }

}