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

import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Date: 23.08.22
 * Time: 11:00
 * @author Frederick Baier
 *
 */
class NodeAPIOnlineStrategyCreateTest : NodeAPIOnlineStrategyBaseTest() {

    @Test
    fun createProcessOnlineCountStrategyWithEmptyName_willFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createStrategy(createStrategyConfigWithName(""))
            }
        }
    }

    @Test
    fun createProcessOnlineCountStrategyWithTooShortName_willFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createStrategy(createStrategyConfigWithName("s"))
            }
        }
    }

    @Test
    fun createProcessOnlineCountStrategyWithNotExistingClass_willFail() {
        val configuration = ProcessOnlineCountStrategyConfiguration(
            "Test",
            "test.NotExistingClass",
            emptySet(),
            emptyMap()
        )
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createStrategy(configuration)
            }
        }
    }

    @Test
    fun createValidProcessOnlineCountStrategy_willNotFail() {
        runBlocking {
            createStrategy(createStrategyConfigWithName("Test"))
        }
    }

    @Test
    fun createValidProcessOnlineCountStrategyTwice_willFail() = runBlocking {
        val configuration = createStrategyConfigWithName("Test")
        createStrategy(configuration)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                createStrategy(configuration)
            }
        }
    }

}