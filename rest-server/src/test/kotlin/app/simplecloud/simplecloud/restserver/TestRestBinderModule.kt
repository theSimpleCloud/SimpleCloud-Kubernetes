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

package app.simplecloud.simplecloud.restserver

import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.api.service.ProcessOnlineCountService
import app.simplecloud.simplecloud.api.validator.ValidatorService
import app.simplecloud.simplecloud.api.validator.ValidatorServiceImpl
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.auth.RestAuthServiceImpl
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import app.simplecloud.simplecloud.restserver.service.TestCloudProcessGroupService
import app.simplecloud.simplecloud.restserver.service.TestCloudProcessService
import app.simplecloud.simplecloud.restserver.service.TestNodeService
import app.simplecloud.simplecloud.restserver.service.TestProcessOnlineCountService
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder

/**
 * Created by IntelliJ IDEA.
 * Date: 27.06.2021
 * Time: 17:01
 * @author Frederick Baier
 */
class TestRestBinderModule : AbstractModule() {


    override fun configure() {
        bind(JwtTokenHandler::class.java).toInstance(JwtTokenHandler("123"))
        bind(AuthService::class.java).to(RestAuthServiceImpl::class.java)


        bind(CloudProcessGroupService::class.java).to(TestCloudProcessGroupService::class.java)
        bind(ProcessOnlineCountService::class.java).to(TestProcessOnlineCountService::class.java)
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