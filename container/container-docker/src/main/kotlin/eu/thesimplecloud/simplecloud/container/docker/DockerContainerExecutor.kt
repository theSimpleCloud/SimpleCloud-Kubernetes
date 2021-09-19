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
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.*
import eu.thesimplecloud.simplecloud.container.ContainerSpec
import eu.thesimplecloud.simplecloud.container.IImage
import org.apache.commons.io.FileUtils
import java.io.Closeable
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 12/08/2021
 * Time: 10:35
 * @author Frederick Baier
 */
class DockerContainerExecutor(
    private val containerName: String,
    private val image: IImage,
    private val containerSpec: ContainerSpec,
    private val dockerClient: DockerClient
) {

    @Volatile
    private var containerId: String? = null

    private val terminationFuture = CompletableFuture<Unit>()

    fun startContainer() {
        println("starting container DockerContainerExecutor")
        val imageId = this.image.build().join()
        println("Built Image: ${imageId}")
        startContainerWithImageId(imageId)
    }

    private fun startContainerWithImageId(imageId: String) {
        val containerId = createContainer(imageId)
        this.containerId = containerId
        startListening()
        startThisContainer()
    }

    private fun startListening() {
        this.dockerClient.eventsCmd()
            .withEventFilter("stop", "die")
            .withEventTypeFilter(EventType.CONTAINER)
            .withContainerFilter(this.containerId)
            .exec(object: ResultCallback<Event> {
                override fun close() {
                }

                override fun onStart(closeable: Closeable?) {
                }

                override fun onNext(event: Event) {
                    this@DockerContainerExecutor.terminationFuture.complete(Unit)
                }

                override fun onError(throwable: Throwable?) {
                }

                override fun onComplete() {
                }

            })
    }

    fun executeCommand(command: String) {
        if (this.containerId == null) throw IllegalStateException("Container is not running")
        this.dockerClient.execCreateCmd(this.containerId!!)
            .withCmd(*command.split(" ").toTypedArray())
            .exec()

    }

    fun shutdownContainer(): CompletableFuture<Unit> {
        if (this.containerId == null) throw IllegalStateException("Container is not running")
        this.dockerClient.stopContainerCmd(this.containerId!!)
            .exec()
        return terminationFuture()
    }

    fun terminationFuture(): CompletableFuture<Unit> {
        return this.terminationFuture
    }

    fun isContainerRunning(): Boolean {
        return this.containerId != null
    }

    fun killContainer() {
        if (this.containerId == null) throw IllegalStateException("Container is not running")
        this.dockerClient.killContainerCmd(this.containerId!!)
            .exec()
    }

    private fun createContainer(imageId: String): String {
        val portBindings = this.containerSpec.portBindings
            .map { PortBinding.parse("${it.hostPort}:${it.containerPort}") }
        val volumeBindings = this.containerSpec.volumeBindings
            .map { Bind.parse("${it.hostPath}:${it.containerPath}") }

        val createContainerCmd = dockerClient.createContainerCmd(imageId)
            .withName(this.containerName)
            .withHostName(this.containerName)
            .withHostConfig(
                HostConfig.newHostConfig()
                    .withPortBindings(portBindings)
                    .withBinds(volumeBindings)
                    .withMemory(this.containerSpec.maxMemory * 1024 * 1024.toLong()) //Memory must be in Bytes
            )

        return createContainerCmd.exec().id
    }

    private fun startThisContainer() {
        this.containerId?.let { this.dockerClient.startContainerCmd(it).exec() }
    }

    fun getLogs(): List<String> {
        TODO()
    }

    fun copyFromContainer(source: String, dest: File) {
        val inputStream = this.dockerClient.copyArchiveFromContainerCmd(this.containerName, source)
            .exec()
        FileUtils.copyInputStreamToFile(inputStream, dest)
    }

}