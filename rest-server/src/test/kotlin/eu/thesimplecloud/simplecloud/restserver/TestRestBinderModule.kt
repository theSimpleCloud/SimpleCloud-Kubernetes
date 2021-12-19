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

package eu.thesimplecloud.simplecloud.restserver

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import eu.thesimplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import eu.thesimplecloud.simplecloud.api.service.*
import eu.thesimplecloud.simplecloud.api.validator.ValidatorService
import eu.thesimplecloud.simplecloud.api.validator.ValidatorServiceImpl
import eu.thesimplecloud.simplecloud.restserver.service.AuthServiceImpl
import eu.thesimplecloud.simplecloud.restserver.service.AuthService
import eu.thesimplecloud.simplecloud.restserver.service.UserService
import eu.thesimplecloud.simplecloud.restserver.service.UserServiceImpl
import eu.thesimplecloud.simplecloud.restserver.service.*

/**
 * Created by IntelliJ IDEA.
 * Date: 27.06.2021
 * Time: 17:01
 * @author Frederick Baier
 */
class TestRestBinderModule : AbstractModule() {


    override fun configure() {
        bind(AuthService::class.java).to(AuthServiceImpl::class.java)
        bind(UserService::class.java).to(UserServiceImpl::class.java)


        bind(CloudProcessGroupService::class.java).to(TestCloudProcessGroupService::class.java)
        bind(JvmArgumentsService::class.java).to(TestJvmArgumentsService::class.java)
        bind(ProcessOnlineCountService::class.java).to(TestProcessOnlineCountService::class.java)
        bind(TemplateService::class.java).to(TestTemplateService::class.java)
        bind(ProcessVersionService::class.java).to(TestProcessVersionService::class.java)
        bind(CloudProcessService::class.java).to(TestCloudProcessService::class.java)
        bind(NodeService::class.java).to(TestNodeService::class.java)

        bind(ValidatorService::class.java).to(ValidatorServiceImpl::class.java)

        install(
            FactoryModuleBuilder()
                .implement(CloudProcess::class.java, CloudProcess::class.java)
                .build(CloudProcessFactory::class.java)
        )
    }


}