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