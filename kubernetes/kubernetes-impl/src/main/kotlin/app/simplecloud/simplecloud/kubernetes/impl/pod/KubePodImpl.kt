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

package app.simplecloud.simplecloud.kubernetes.impl.pod

import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import io.kubernetes.client.openapi.apis.CoreV1Api

/**
 * Date: 28.04.22
 * Time: 18:02
 * @author Frederick Baier
 *
 */
class KubePodImpl(
    private val name: String,
    private val api: CoreV1Api
) : KubePod {

    private val executor = KubernetesPodExecutor(this.name, this.api)

    override fun getName(): String {
        return this.name
    }

    override fun execute(command: String) {
        this.executor.executeCommand(command)
    }

    override fun start(podSpec: PodSpec) {
        this.executor.startContainer(podSpec)
    }

    override fun delete() {
        this.executor.deleteContainer()
    }

    override fun forceShutdown() {
        this.executor.killContainer()
    }

    override fun exists(): Boolean {
        return this.executor.doesContainerExist()
    }

    override fun isActive(): Boolean {
        val phase = this.executor.getPhase()
        return phase == "Pending" || phase == "Running"
    }

    override fun getLogs(): List<String> {
        return this.executor.getLogs()
    }

    override fun getLabels(): List<Label> {
        return this.executor.getLabels()
    }
}