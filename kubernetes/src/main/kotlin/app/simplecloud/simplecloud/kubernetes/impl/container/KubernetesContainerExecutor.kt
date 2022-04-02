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

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.future.timeout.timout
import app.simplecloud.simplecloud.kubernetes.api.container.ContainerSpec
import io.kubernetes.client.Attach
import io.kubernetes.client.PodLogs
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Pod
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CopyOnWriteArrayList


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
        val attach = Attach(this.api.apiClient)
        val result = attach.attach("default", this.containerName, true)
        val output = result.standardInputStream
        output.write(command.toByteArray(StandardCharsets.UTF_8))
        output.write('\n'.code)
        output.flush()
    }

    fun getLogs(): List<String> {
        val logs = PodLogs(this.api.apiClient)
        val pod: V1Pod = this.api.readNamespacedPod(this.containerName, "default", null)
        val inputStream = logs.streamNamespacedPodLog(pod)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        val list = readLinesBlocking(bufferedReader)
        inputStream.close()
        return list
    }

    private fun readLinesBlocking(bufferedReader: BufferedReader): List<String> {
        val list = CopyOnWriteArrayList<String>()
        runBlocking {
            while (true) {
                val line = readSingleLine(bufferedReader) ?: break
                list.add(line)
            }
        }
        return list
    }

    private suspend fun readSingleLine(bufferedReader: BufferedReader): String? {
        val future = CloudCompletableFuture.supplyAsync { bufferedReader.readLine() }.nonNull()
        future.timout(50)
        return runCatching { future.await() }.getOrNull()
    }

    fun deleteContainerOnShutdown() {

    }
}
