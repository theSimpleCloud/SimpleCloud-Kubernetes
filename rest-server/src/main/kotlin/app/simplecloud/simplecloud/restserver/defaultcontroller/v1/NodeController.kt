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

import app.simplecloud.simplecloud.api.node.configuration.NodeConfiguration
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.restserver.annotation.RequestMapping
import app.simplecloud.simplecloud.restserver.annotation.RequestPathParam
import app.simplecloud.simplecloud.restserver.annotation.RestController
import app.simplecloud.simplecloud.restserver.base.route.RequestType
import app.simplecloud.simplecloud.restserver.controller.Controller
import com.google.inject.Inject
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 16/07/2021
 * Time: 11:08
 * @author Frederick Baier
 */
@RestController(1, "cloud/node")
class NodeController @Inject constructor(
    private val nodeService: NodeService
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.cloud.node.get")
    fun handleGetAll(): List<NodeConfiguration> {
        val processes = this.nodeService.findAll().join()
        return processes.map { it.toConfiguration() }
    }

    @RequestMapping(RequestType.GET, "{uuid}", "web.cloud.node.get")
    fun handleGetOne(@RequestPathParam("uuid") uuid: String): NodeConfiguration {
        val process = this.nodeService.findByUniqueId(UUID.fromString(uuid)).join()
        return process.toConfiguration()
    }

}