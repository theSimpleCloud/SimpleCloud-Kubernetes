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

package app.simplecloud.simplecloud.node.api.onlinestrategy

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.module.api.internal.service.InternalNodeCloudAPI
import app.simplecloud.simplecloud.module.api.service.NodeProcessOnlineStrategyService
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 23.08.22
 * Time: 11:00
 * @author Frederick Baier
 *
 */
open class NodeAPIOnlineStrategyBaseTest {

    private val nodeAPIBaseTest = NodeAPIBaseTest()

    protected lateinit var cloudAPI: InternalNodeCloudAPI

    protected lateinit var onlineStrategyService: NodeProcessOnlineStrategyService

    @BeforeEach
    open fun setUp() {
        this.nodeAPIBaseTest.setUp()
        this.cloudAPI = this.nodeAPIBaseTest.cloudAPI
        this.onlineStrategyService = this.nodeAPIBaseTest.cloudAPI.getOnlineStrategyService()
    }

    @AfterEach
    fun tearDown() {
        this.nodeAPIBaseTest.tearDown()
    }


    protected fun createStrategyConfigWithName(name: String): ProcessOnlineCountStrategyConfiguration {
        return ProcessOnlineCountStrategyConfiguration(
            name,
            "app.simplecloud.simplecloud.node.api.onlinestrategy.TestOnlineCountStrategy",
            emptyMap()
        )
    }

    protected suspend fun createStrategy(configuration: ProcessOnlineCountStrategyConfiguration) {
        this.onlineStrategyService.createCreateRequest(
            configuration
        ).submit().await()
    }

}