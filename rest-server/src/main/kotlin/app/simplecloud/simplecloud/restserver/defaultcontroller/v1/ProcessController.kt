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

package app.simplecloud.simplecloud.restserver.defaultcontroller.v1

import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.restserver.annotation.RequestBody
import app.simplecloud.simplecloud.restserver.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.annotation.RestController
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.controller.Controller
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.dto.CloudProcessCreateRequestDto
import app.simplecloud.simplecloud.restserver.defaultcontroller.v1.handler.ProcessCreateHandler
import com.google.inject.Inject
import kotlinx.coroutines.runBlocking

/**
 * Created by IntelliJ IDEA.
 * Date: 09/07/2021
 * Time: 19:57
 * @author Frederick Baier
 */
@RestController(1, "cloud/process")
class ProcessController @Inject constructor(
    private val processService: CloudProcessService,
    private val processCreateHandler: ProcessCreateHandler
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.process.get")
    fun handleGetAll(): List<CloudProcessConfiguration> {
        val processes = this.processService.findAll().join()
        return processes.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.process.get")
    fun handleGetOne(@RequestPathParam("name") name: String): CloudProcessConfiguration {
        val process = this.processService.findProcessByName(name).join()
        return process.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.process.create")
    fun handleCreate(@RequestBody configuration: CloudProcessCreateRequestDto): CloudProcessConfiguration = runBlocking {
        return@runBlocking processCreateHandler.create(configuration).toConfiguration()
    }

    @RequestMapping(RequestType.DELETE, "{name}", "web.cloud.process.delete")
    fun handleShutdown(@RequestPathParam("name") name: String): Boolean {
        val process = this.processService.findProcessByName(name).join()
        process.createShutdownRequest().submit()
        return true
    }

}