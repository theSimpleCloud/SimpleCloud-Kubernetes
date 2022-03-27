/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.restserver

import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.api.validator.ValidatorService
import app.simplecloud.simplecloud.api.validator.ValidatorServiceImpl
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.auth.RestAuthServiceImpl
import app.simplecloud.simplecloud.restserver.base.service.AuthService
import app.simplecloud.simplecloud.restserver.service.TestCloudProcessGroupService
import app.simplecloud.simplecloud.restserver.service.TestCloudProcessService
import app.simplecloud.simplecloud.restserver.service.TestNodeService
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