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
import app.simplecloud.simplecloud.kubernetes.api.exception.KubeException
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import io.kubernetes.client.Attach
import io.kubernetes.client.PodLogs
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.util.Streams
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets


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
            fetchThisPod()
        }.isSuccess
    }

    fun getPhase(): String {
        return fetchThisPod().status?.phase ?: "Unknown"
    }

    fun deleteContainer() {
        killContainer(60)
    }

    fun killContainer(timeBeforeKillInSeconds: Int = 1) {
        try {
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
        } catch (ex: ApiException) {
            throw KubeException(ex.responseBody, ex)
        }
    }


    fun executeCommand(command: String) {
        val result = attachPod()
        val output = result.standardInputStream
        output.write(command.toByteArray(StandardCharsets.UTF_8))
        output.write('\n'.code)
        output.flush()
        output.close()
    }

    private fun attachPod(): Attach.AttachResult {
        try {
            val attach = Attach(this.api.apiClient)
            return attach.attach("default", this.containerName, true)
        } catch (ex: ApiException) {
            throw KubeException(ex.responseBody, ex)
        }
    }

    fun getLogs(): String {
        val pod: V1Pod = fetchThisPod()
        val logs = PodLogs()
        val inputStream: InputStream = logs.streamNamespacedPodLog(pod)
        return Streams.toString(InputStreamReader(inputStream))
    }

    private suspend fun readSingleLine(bufferedReader: BufferedReader): String? {
        val future = CloudCompletableFuture.supplyAsync { bufferedReader.readLine() }.nonNull()
        future.timout(50)
        return runCatching { future.await() }.getOrNull()
    }

    fun getLabels(): List<Label> {
        val pod = fetchThisPod()
        return pod.metadata?.labels?.map { Label(it.key, it.value) } ?: emptyList()
    }

    private fun fetchThisPod(): V1Pod {
        try {
            return this.api.readNamespacedPod(this.containerName, "default", null)
        } catch (ex: ApiException) {
            throw KubeException(ex.responseBody, ex)
        }
    }

}
