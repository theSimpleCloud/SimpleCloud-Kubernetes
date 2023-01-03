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

package app.simplecloud.simplecloud.node.update

import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import org.junit.jupiter.api.*
import java.util.concurrent.TimeUnit

/**
 * Date: 02.01.23
 * Time: 15:26
 * @author Frederick Baier
 *
 */
class NodeUpdateTest : NodeAPIBaseTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        try {
            this.kubeAPI.getPodService().getPod("updater").delete()
        } catch (ex: Exception) {
        }
    }

    @Test
    @Timeout(1, unit = TimeUnit.SECONDS)
    fun dontExecuteUpdater_updatePodWillNotExist() {
        Assertions.assertThrows(NoSuchElementException::class.java) {
            this.kubeAPI.getPodService().getPod("updater")
        }
    }

    @Test
    @Timeout(1, unit = TimeUnit.SECONDS)
    fun executeUpdater_cloudWillBeInDisableMode() {
        executeUpdater()
        Assertions.assertTrue(this.cloudAPI.isDisabledMode().get())
    }

    @Test
    @Timeout(1, unit = TimeUnit.SECONDS)
    fun executeUpdater_updaterPodWillBeStarted() {
        executeUpdater()
        assertPodExists("updater")
    }

    @Test
    @Timeout(1, unit = TimeUnit.SECONDS)
    fun cloudAlreadyDisabled_executeUpdater_willFail() {
        this.cloudAPI.setDisabledMode(true)
        Assertions.assertThrows(NodeDisabler.AlreadyDisabledException::class.java) {
            executeUpdater()
        }
    }

    private fun executeUpdater() {
        val nodeUpdater =
            NodeUpdater(emptyList(), "simplecloud-base", "buildkit.addr", "simplecloud-dest", this.cloudAPI)
        nodeUpdater.executeUpdate()
    }

    private fun assertPodExists(name: String) {
        this.kubeAPI.getPodService().getPod(name)
    }

}