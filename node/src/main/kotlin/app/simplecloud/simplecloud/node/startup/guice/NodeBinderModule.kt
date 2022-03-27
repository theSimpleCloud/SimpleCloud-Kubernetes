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

package app.simplecloud.simplecloud.node.startup.guice

import app.simplecloud.simplecloud.api.service.NodeProcessOnlineStrategyService
import app.simplecloud.simplecloud.node.process.ProcessShutdownHandler
import app.simplecloud.simplecloud.node.process.ProcessShutdownHandlerImpl
import app.simplecloud.simplecloud.node.process.ProcessStarter
import app.simplecloud.simplecloud.node.process.ProcessStarterImpl
import app.simplecloud.simplecloud.node.service.NodeProcessOnlineStrategyServiceImpl
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder

/**
 * Created by IntelliJ IDEA.
 * Date: 07/08/2021
 * Time: 11:27
 * @author Frederick Baier
 */
class NodeBinderModule() : AbstractModule() {

    override fun configure() {
        bind(NodeProcessOnlineStrategyService::class.java).to(NodeProcessOnlineStrategyServiceImpl::class.java)

        install(
            FactoryModuleBuilder()
                .implement(ProcessStarter::class.java, ProcessStarterImpl::class.java)
                .build(ProcessStarter.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(ProcessShutdownHandler::class.java, ProcessShutdownHandlerImpl::class.java)
                .build(ProcessShutdownHandler.Factory::class.java)
        )
    }

}