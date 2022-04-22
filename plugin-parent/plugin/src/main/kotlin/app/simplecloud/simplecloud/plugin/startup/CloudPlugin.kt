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

package app.simplecloud.simplecloud.plugin.startup

import app.simplecloud.simplecloud.api.impl.guice.CloudAPIBinderModule
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionFactory
import app.simplecloud.simplecloud.plugin.startup.service.*
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector

class CloudPlugin(
    private val guiceModule: AbstractModule,
    private val distributionFactory: DistributionFactory
) {

    val injector: Injector

    init {
        val distribution = startDistribution()
        val intermediateInjector = Guice.createInjector(
            CloudAPIBinderModule(
                distribution,
                NodeServiceImpl::class.java,
                CloudProcessServiceImpl::class.java,
                CloudProcessGroupServiceImpl::class.java,
                CloudPlayerServiceImpl::class.java,
                PermissionGroupServiceImpl::class.java
            )
        )
        this.injector = intermediateInjector.createChildInjector(
            this.guiceModule
        )
        this.injector.getInstance(SelfDistributedProcessUpdater::class.java).updateProcessBlocking()
    }

    private fun startDistribution(): Distribution {
        val nodeAddress = Address.fromIpString("distribution:1670")
        return this.distributionFactory.createClient(nodeAddress)
    }

}