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

package app.simplecloud.simplecloud.node.api.process

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.process.CloudProcess
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * Date: 12.05.22
 * Time: 19:54
 * @author Frederick Baier
 *
 */
class NodeAPIProcessUpdateTest : NodeAPIProcessTest() {

    private lateinit var process: CloudProcess

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.process = this.processService.createStartRequest(this.defaultGroup).submit().join()
    }

    @Test
    fun updateMaxPlayersTest() {
        val maxPlayers = Random.nextInt(200)
        this.processService.createUpdateRequest(process)
            .setMaxPlayers(maxPlayers)
            .submit().join()
        Assertions.assertEquals(maxPlayers, getCurrentProcess().getMaxPlayers())
    }

    @Test
    fun updateMaxPlayersWithInfinitePlayers() {
        this.processService.createUpdateRequest(process)
            .setMaxPlayers(-1)
            .submit().join()
        Assertions.assertEquals(-1, getCurrentProcess().getMaxPlayers())
    }

    @Test
    fun updateMaxPlayersWithTooLowNumber_willFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                processService.createUpdateRequest(process)
                    .setMaxPlayers(-2)
                    .submit().await()
            }
        }
    }

    @Test
    fun updateVisibleTest() {
        val visible = Random.nextBoolean()
        this.processService.createUpdateRequest(process)
            .setVisible(visible)
            .submit().join()
        Assertions.assertEquals(visible, getCurrentProcess().isVisible())
    }

    private fun getCurrentProcess(): CloudProcess {
        return this.processService.findByName(process.getName()).join()
    }


}