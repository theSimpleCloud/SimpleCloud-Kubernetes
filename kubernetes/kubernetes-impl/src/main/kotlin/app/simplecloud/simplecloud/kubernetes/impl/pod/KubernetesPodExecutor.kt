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

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.nonNull
import app.simplecloud.simplecloud.api.future.timeout.timout
import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import io.kubernetes.client.Attach
import io.kubernetes.client.PodLogs
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Pod
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CopyOnWriteArrayList


class KubernetesPodExecutor(
    private val containerName: String,
    private val api: CoreV1Api,
) {

    fun startContainer(podSpec: PodSpec) {
        if (doesContainerExist()) throw IllegalStateException("Container does already exist")

        KubernetesPodStarter(this.containerName, podSpec, this.api)
            .startContainer()
    }

    fun doesContainerExist(): Boolean {
        return runCatching {
            this.api.readNamespacedPod(this.containerName, "default", null)
        }.isSuccess
    }

    fun getPhase(): String {
        return this.api.readNamespacedPod(this.containerName, "default", null).status?.phase ?: "Unknown"
    }

    fun deleteContainer() {
        killContainer(60)
    }

    fun killContainer(timeBeforeKillInSeconds: Int = 1) {
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

    fun getLabels(): List<Label> {
        val pod = this.api.readNamespacedPod(this.containerName, "default", null)
        return pod.metadata?.labels?.map { Label(it.key, it.value) } ?: emptyList()
    }
}
