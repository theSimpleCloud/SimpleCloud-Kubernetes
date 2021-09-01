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
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import eu.thesimplecloud.simplecloud.api.future.cloud.nonNull
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import eu.thesimplecloud.simplecloud.container.ImageBuildInstructions
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 11/08/2021
 * Time: 17:22
 * @author Frederick Baier
 */
class DockerImageBuilder(
    private val name: String,
    private val buildDir: File,
    private val buildInstructions: ImageBuildInstructions,
    private val dockerClient: DockerClient
) {

    @Volatile
    private var buildFuture: CompletableFuture<String>? = null

    fun isBuilt(): Boolean {
        return this.buildFuture?.isDone ?: false
    }

    fun build(): CompletableFuture<String> {
        return if (hasBuildStarted()) {
            this.buildFuture!!
        } else {
            this.startBuild()
        }
    }

    private fun hasBuildStarted(): Boolean {
        return this.buildFuture != null
    }

    private fun startBuild(): CompletableFuture<String> {
        val buildFuture = runBuildInCompletableFuture()
        this.buildFuture = buildFuture
        return buildFuture
    }

    private fun runBuildInCompletableFuture(): CompletableFuture<String> {
        return CloudCompletableFuture.supplyAsync {
            return@supplyAsync executeBuild()
        }.nonNull()
    }

    private fun executeBuild(): String {
        createDockerFile()
        val command = dockerClient.buildImageCmd()
            .withBaseDirectory(this.buildDir)
            .withDockerfile(File(this.buildDir, "Dockerfile"))
            .withTags(setOf(this.name))

        return command.start().awaitImageId()
    }

    private fun createDockerFile(): File {
        val buildInstructionsAsString = this.buildInstructions.getAllInstructions().joinToString("\n")
        val dockerFile = File(this.buildDir, "Dockerfile")
        dockerFile.writeText(buildInstructionsAsString)
        return dockerFile
    }
}