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
import eu.thesimplecloud.simplecloud.api.service.TemplateService
import eu.thesimplecloud.simplecloud.api.template.configuration.TemplateConfiguration
import eu.thesimplecloud.simplecloud.restserver.annotation.*
import eu.thesimplecloud.simplecloud.restserver.controller.Controller

/**
 * Created by IntelliJ IDEA.
 * Date: 11/07/2021
 * Time: 22:09
 * @author Frederick Baier
 */
@RestController(1, "cloud/template")
class TemplateController @Inject constructor(
    private val templateService: TemplateService,
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.template.get")
    fun handleGetAll(): List<TemplateConfiguration> {
        val list = await(this.templateService.findAll())
        return list.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.template.get")
    fun handleGetOne(@RequestPathParam("name") name: String): TemplateConfiguration {
        val template = await(this.templateService.findByName(name))
        return template.toConfiguration()
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.template.create")
    fun handleCreate(@RequestBody configuration: TemplateConfiguration): Boolean {
        val request = this.templateService.createTemplateCreateRequest(configuration)
        await(request.submit())
        return true
    }

    @RequestMapping(RequestType.DELETE, "{name}", "web.cloud.template.delete")
    fun handleDelete(@RequestPathParam("name") name: String): Boolean {
        val template = await(this.templateService.findByName(name))
        val request = this.templateService.createTemplateDeleteRequest(template)
        await(request.submit())
        return true
    }


}