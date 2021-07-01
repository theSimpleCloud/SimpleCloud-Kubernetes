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

import com.google.inject.Inject
import eu.thesimplecloud.simplecloud.api.impl.future.getOrThrowRealExceptionOnFailure
import eu.thesimplecloud.simplecloud.api.service.ICloudProcessGroupService
import eu.thesimplecloud.simplecloud.restserver.annotation.*
import eu.thesimplecloud.simplecloud.restserver.controller.IController
import eu.thesimplecloud.simplecloud.restserver.dto.cloud.BasicCloudProcessGroupDto
import eu.thesimplecloud.simplecloud.restserver.dto.cloud.GroupToDtoConverter

/**
 * Created by IntelliJ IDEA.
 * Date: 28.06.2021
 * Time: 12:43
 * @author Frederick Baier
 */
@Controller(1, "cloud/group")
class ProcessGroupController @Inject constructor(
    private val groupService: ICloudProcessGroupService
) : IController {

    @RequestMapping(RequestType.GET, "", "web.cloud.group.get.all")
    fun handleGroupGetAll(): List<BasicCloudProcessGroupDto> {
        val groups = this.groupService.findAll().getOrThrowRealExceptionOnFailure()
        return groups.map { GroupToDtoConverter(it).convert() }
    }

    @RequestMapping(RequestType.GET, "{name}", "web.cloud.group.get.one")
    fun handleGroupGetOne(@RequestPathParam("name") name: String): BasicCloudProcessGroupDto {
        val group = this.groupService.findByName(name).getOrThrowRealExceptionOnFailure()
        return GroupToDtoConverter(group).convert()
    }

}