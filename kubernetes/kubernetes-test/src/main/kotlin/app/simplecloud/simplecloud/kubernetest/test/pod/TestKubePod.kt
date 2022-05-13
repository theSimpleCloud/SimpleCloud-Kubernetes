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

package app.simplecloud.simplecloud.kubernetest.test.pod

import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import java.util.concurrent.CopyOnWriteArrayList

class TestKubePod(
    private val name: String,
    private val kubeService: TestKubePodService
) : KubePod {

    private val executedCommands = CopyOnWriteArrayList<String>()

    @Volatile
    private var podSpec: PodSpec? = null

    private var isRunning = false

    override fun getName(): String {
        return this.name
    }

    override fun execute(command: String) {
        this.executedCommands.add(command)
    }

    override fun start(podSpec: PodSpec) {
        this.isRunning = true
        this.podSpec = podSpec
    }

    override fun shutdown() {
        this.kubeService.delete(this)
        this.isRunning = false
    }

    override fun forceShutdown() {
        shutdown()
    }

    override fun isRunning(): Boolean {
        return this.isRunning
    }

    override fun getLogs(): List<String> {
        TODO("Not yet implemented")
    }

    override fun getLabels(): List<Label> {
        return this.podSpec?.labels ?: emptyList()
    }

    fun getExecutedCommands(): List<String> {
        return this.executedCommands
    }

}
