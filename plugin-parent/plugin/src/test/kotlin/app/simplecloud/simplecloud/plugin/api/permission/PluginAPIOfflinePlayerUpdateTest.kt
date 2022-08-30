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

package app.simplecloud.simplecloud.plugin.api.permission

import app.simplecloud.simplecloud.api.internal.InternalCloudAPI
import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.node.api.player.OfflinePlayerUpdateTest
import app.simplecloud.simplecloud.plugin.proxy.ProxyPluginBaseTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 28.08.22
 * Time: 11:37
 * @author Frederick Baier
 *
 */
class PluginAPIOfflinePlayerUpdateTest : OfflinePlayerUpdateTest() {


    private val pluginBaseTest = ProxyPluginBaseTest()

    @BeforeEach
    override fun setUp() {
        pluginBaseTest.setUp()
        super.setUp()
    }

    override fun getInMemoryDatabaseFactory(): InMemoryRepositorySafeDatabaseFactory {
        return pluginBaseTest.nodeAPIBaseTest.databaseFactory
    }

    @AfterEach
    fun tearDown() {
        pluginBaseTest.tearDown()
    }

    override fun getCloudAPI(): InternalCloudAPI {
        return pluginBaseTest.pluginCloudAPI
    }

}