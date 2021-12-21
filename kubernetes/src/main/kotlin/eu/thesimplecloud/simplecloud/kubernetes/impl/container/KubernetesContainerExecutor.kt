/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.kubernetes.impl.container

import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import eu.thesimplecloud.simplecloud.kubernetes.api.container.ContainerSpec
import eu.thesimplecloud.simplecloud.api.image.Image
import io.kubernetes.client.openapi.apis.CoreV1Api
import java.lang.IllegalStateException
import java.util.concurrent.CompletableFuture


class KubernetesContainerExecutor(
    private val containerName: String,
    private val image: Image,
    private val containerSpec: ContainerSpec,
    private val api: CoreV1Api
) {

    private val terminationFuture = CloudCompletableFuture<Unit>()

    @Volatile
    private var wasStarted = false

    fun terminationFuture(): CompletableFuture<Unit> {
        return this.terminationFuture
    }

    fun startContainer() {
        if (this.wasStarted) throw IllegalStateException("Cannot start container twice")
        this.wasStarted = true

        KubernetesContainerStarter(this.containerName, this.image, this.containerSpec, this.api)
            .startContainer()
    }

    fun shutdownContainer(): CompletableFuture<Unit> {
        killContainer()
        return this.terminationFuture
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
        return this.wasStarted && !this.terminationFuture.isDone
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