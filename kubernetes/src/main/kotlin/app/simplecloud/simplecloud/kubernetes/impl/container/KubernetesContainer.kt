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

package app.simplecloud.simplecloud.kubernetes.impl.container

import app.simplecloud.simplecloud.kubernetes.api.container.Container
import app.simplecloud.simplecloud.kubernetes.api.container.ContainerSpec
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import io.kubernetes.client.openapi.apis.CoreV1Api
import java.io.File

class KubernetesContainer @Inject constructor(
    @Assisted private val name: String,
    private val api: CoreV1Api,
) : Container {

    private val executor = KubernetesContainerExecutor(this.name, this.api)

    override fun getName(): String {
        return this.name
    }

    override fun execute(command: String) {
        this.executor.executeCommand(command)
    }

    override fun start(containerSpec: ContainerSpec) {
        this.executor.startContainer(containerSpec)
    }

    override fun shutdown() {
        this.executor.shutdownContainer()
    }

    override fun forceShutdown() {
        this.executor.killContainer()
    }

    override fun isRunning(): Boolean {
        return this.executor.isContainerRunning()
    }

    override fun getLogs(): List<String> {
        return this.executor.getLogs()
    }

    override fun copyFromContainer(source: String, dest: File) {
        TODO("Not yet implemented")
    }

    override fun deleteOnShutdown() {
        this.executor.deleteContainerOnShutdown()
    }
}