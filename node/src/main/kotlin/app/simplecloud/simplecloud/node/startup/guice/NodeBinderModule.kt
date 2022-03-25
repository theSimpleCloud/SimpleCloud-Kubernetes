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

package app.simplecloud.simplecloud.node.startup.guice

import app.simplecloud.simplecloud.node.onlinestrategy.NodeProcessOnlineStrategyRegistry
import app.simplecloud.simplecloud.node.onlinestrategy.NodeProcessOnlineStrategyRegistryImpl
import app.simplecloud.simplecloud.node.onlinestrategy.NodeProcessOnlineStrategyService
import app.simplecloud.simplecloud.node.onlinestrategy.NodeProcessOnlineStrategyServiceImpl
import app.simplecloud.simplecloud.node.process.ProcessStarter
import app.simplecloud.simplecloud.node.process.ProcessStarterImpl
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

        bind(NodeProcessOnlineStrategyRegistry::class.java).to(NodeProcessOnlineStrategyRegistryImpl::class.java)
        bind(NodeProcessOnlineStrategyService::class.java).to(NodeProcessOnlineStrategyServiceImpl::class.java)

        install(
            FactoryModuleBuilder()
                .implement(ProcessStarter::class.java, ProcessStarterImpl::class.java)
                .build(ProcessStarter.Factory::class.java)
        )
    }

}