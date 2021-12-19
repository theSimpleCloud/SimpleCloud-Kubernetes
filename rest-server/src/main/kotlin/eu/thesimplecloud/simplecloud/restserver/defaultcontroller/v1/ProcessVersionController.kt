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
import eu.thesimplecloud.simplecloud.api.process.version.configuration.ProcessVersionConfiguration
import eu.thesimplecloud.simplecloud.api.service.ProcessVersionService
import eu.thesimplecloud.simplecloud.restserver.annotation.*
import eu.thesimplecloud.simplecloud.restserver.controller.Controller

/**
 * Created by IntelliJ IDEA.
 * Date: 11/07/2021
 * Time: 22:09
 * @author Frederick Baier
 */
@RestController(1, "cloud/processversion")
class ProcessVersionController @Inject constructor(
    private val processVersionService: ProcessVersionService,
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.processversion.get")
    fun handleGetAll(): List<ProcessVersionConfiguration> {
        val versions = await(this.processVersionService.findAll())
        return versions.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.processversion.get")
    fun handleGetOne(@RequestPathParam("name") name: String): ProcessVersionConfiguration {
        val version = await(this.processVersionService.findByName(name))
        return version.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.processversion.create")
    fun handlePost(@RequestBody configuration: ProcessVersionConfiguration): Boolean {
        val request = this.processVersionService.createProcessVersionCreateRequest(configuration)
        await(request.submit())
        return true
    }

    @RequestMapping(RequestType.DELETE, "{name}", "web.cloud.processversion.create")
    fun handleDelete(@RequestPathParam("name") name: String): Boolean {
        val processVersion = await(this.processVersionService.findByName(name))
        val request = this.processVersionService.createProcessVersionDeleteRequest(processVersion)
        await(request.submit())
        return true
    }

}