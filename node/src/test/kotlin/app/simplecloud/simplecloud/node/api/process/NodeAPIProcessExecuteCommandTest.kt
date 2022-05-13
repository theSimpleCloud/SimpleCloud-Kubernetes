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

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.node.api.assertContains
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 12.05.22
 * Time: 19:54
 * @author Frederick Baier
 *
 */
class NodeAPIProcessExecuteCommandTest : NodeAPIProcessTest() {

    private lateinit var process: CloudProcess

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.process = this.processService.createStartRequest(this.defaultGroup).submit().join()
    }

    @Test
    fun commandExecuteTest() {
        this.processService.createExecuteCommandRequest(process, "mycommand").submit().join()
        val podByProcess = this.kubeAPI.getPodService().getPod(process.getName())
        assertContains(podByProcess.getExecutedCommands(), "mycommand")
    }


}