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

package eu.thesimplecloud.simplecloud.container.docker

import com.github.dockerjava.api.DockerClient
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import eu.thesimplecloud.simplecloud.container.ContainerSpec
import eu.thesimplecloud.simplecloud.container.IContainer
import eu.thesimplecloud.simplecloud.container.IImage
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 11/08/2021
 * Time: 15:01
 * @author Frederick Baier
 */
class DockerContainer @Inject constructor(
    @Assisted private val name: String,
    @Assisted private val image: IImage,
    @Assisted private val containerSpec: ContainerSpec,
    private val dockerClient: DockerClient
) : IContainer {

    private val executor = DockerContainerExecutor(name, image, containerSpec, dockerClient)

    override fun getName(): String {
        return this.name
    }

    override fun getImage(): IImage {
        return this.image
    }

    override fun execute(command: String) {
        this.executor.executeCommand(command)
    }

    override fun start() {
        println("DockerContainer start")
        this.executor.startContainer()
    }

    override fun shutdown(): CompletableFuture<Unit> {
        return this.executor.shutdownContainer()
    }

    override fun terminationFuture(): CompletableFuture<Unit> {
        return this.executor.terminationFuture()
    }

    override fun forceShutdown() {
        return this.executor.killContainer()
    }

    override fun isRunning(): Boolean {
        return this.executor.isContainerRunning()
    }

    override fun getLogs(): List<String> {
        return this.executor.getLogs()
    }
}