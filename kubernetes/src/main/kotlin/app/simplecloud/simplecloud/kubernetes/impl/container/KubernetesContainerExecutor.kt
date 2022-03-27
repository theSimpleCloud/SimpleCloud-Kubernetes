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

import app.simplecloud.simplecloud.kubernetes.api.container.ContainerSpec
import io.kubernetes.client.openapi.apis.CoreV1Api


class KubernetesContainerExecutor(
    private val containerName: String,
    private val api: CoreV1Api
) {

    fun startContainer(containerSpec: ContainerSpec) {
        if (doesContainerExist()) throw IllegalStateException("Container does already exist")

        KubernetesContainerStarter(this.containerName, containerSpec, this.api)
            .startContainer()
    }

    private fun doesContainerExist(): Boolean {
        return runCatching {
            this.api.readNamespacedPod(this.containerName, "default", null)
        }.isSuccess
    }

    fun shutdownContainer() {
        killContainer()
    }

    fun killContainer() {
        val timeBeforeKillInSeconds = 60
        this.api.deleteNamespacedPod(
            this.containerName,
            "default",
            null,
            null,
            timeBeforeKillInSeconds,
            null,
            null,
            null
        )
    }

    fun isContainerRunning(): Boolean {
        return doesContainerExist()
    }


    fun executeCommand(command: String) {
        TODO()
    }

    fun getLogs(): List<String> {
        TODO()
    }

    fun deleteContainerOnShutdown() {

    }


}