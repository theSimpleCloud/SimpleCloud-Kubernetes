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

package eu.thesimplecloud.simplecloud.node.startup.guice

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import dev.morphia.Datastore
import eu.thesimplecloud.simplecloud.api.impl.process.CloudProcessImpl
import eu.thesimplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import eu.thesimplecloud.simplecloud.kubernetes.api.service.KubeService
import eu.thesimplecloud.simplecloud.kubernetes.impl.service.KubeServiceImpl
import eu.thesimplecloud.simplecloud.node.process.ProcessStarter
import eu.thesimplecloud.simplecloud.node.process.ProcessStarterImpl
import eu.thesimplecloud.simplecloud.restserver.repository.UserRepository
import eu.thesimplecloud.simplecloud.restserver.repository.MongoUserRepository

/**
 * Created by IntelliJ IDEA.
 * Date: 07/08/2021
 * Time: 11:27
 * @author Frederick Baier
 */
class NodeBinderModule() : AbstractModule() {

    override fun configure() {
        install(
            FactoryModuleBuilder()
                .implement(CloudProcess::class.java, CloudProcessImpl::class.java)
                .build(CloudProcessFactory::class.java)
        )
        install(
            FactoryModuleBuilder()
                .implement(ProcessStarter::class.java, ProcessStarterImpl::class.java)
                .build(ProcessStarter.Factory::class.java)
        )
    }

}