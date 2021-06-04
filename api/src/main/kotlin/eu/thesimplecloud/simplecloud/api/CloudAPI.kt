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

package eu.thesimplecloud.simplecloud.api

import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.repository.group.ICloudProcessGroupRepository
import eu.thesimplecloud.simplecloud.api.service.group.ICloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.service.jvmargs.IJvmArgumentsService
import eu.thesimplecloud.simplecloud.api.service.node.INodeService
import eu.thesimplecloud.simplecloud.api.service.onlinecount.IProcessOnlineCountService
import eu.thesimplecloud.simplecloud.api.service.process.ICloudProcessService
import eu.thesimplecloud.simplecloud.api.service.processversion.IProcessVersionService
import eu.thesimplecloud.simplecloud.api.service.template.ITemplateService

/**
 * Created by IntelliJ IDEA.
 * Date: 28.03.2021
 * Time: 10:31
 * @author Frederick Baier
 */
abstract class CloudAPI {

    init {
        instance = this
    }


    abstract fun getProcessGroupService(): ICloudProcessGroupService

    abstract fun getProcessService(): ICloudProcessService

    abstract fun getTemplateService(): ITemplateService

    abstract fun getProcessVersionService(): IProcessVersionService

    abstract fun getJvmArgumentsService(): IJvmArgumentsService

    abstract fun getNodeService(): INodeService

    abstract fun getProcessOnlineCountService(): IProcessOnlineCountService


    companion object {
        @JvmStatic
        lateinit var instance: CloudAPI
            private set
    }

}