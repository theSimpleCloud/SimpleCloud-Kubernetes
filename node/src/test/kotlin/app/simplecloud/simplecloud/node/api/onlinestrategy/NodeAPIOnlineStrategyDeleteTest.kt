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

import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 23.08.22
 * Time: 11:00
 * @author Frederick Baier
 *
 */
class NodeAPIOnlineStrategyDeleteTest : NodeAPIOnlineStrategyBaseTest() {

    private lateinit var existingStrategy: ProcessesOnlineCountStrategy

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.existingStrategy = this.onlineStrategyService.createCreateRequest(createStrategyConfigWithName("TestN"))
            .submit().join()
    }

    @Test
    fun doNothing_StrategyWillStillExist() {
        Assertions.assertEquals(1, getOnlineStrategyCount())
    }

    @Test
    fun deleteStrategy_StrategyWillNoLongerExist() {
        deleteStrategy(existingStrategy)
        Assertions.assertEquals(0, getOnlineStrategyCount())
    }

    @Test
    fun createTwo_deleteOne_OneStrategyWillStillExist() = runBlocking {
        createStrategy(createStrategyConfigWithName("NewStrategy"))
        deleteStrategy(existingStrategy)
        Assertions.assertEquals(1, getOnlineStrategyCount())
    }

    private fun deleteStrategy(strategy: ProcessesOnlineCountStrategy) {
        this.onlineStrategyService.createDeleteRequest(strategy).submit().join()
    }

    private fun getOnlineStrategyCount(): Int {
        return this.onlineStrategyService.findAll().join().size
    }


}