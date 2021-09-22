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

package eu.thesimplecloud.simplecloud.node.process.container

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.container.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

class SingleMountingProcessStarter(
    private val imageRepository: IImageRepository,
    private val containerFactory: IContainer.Factory,
    private val imageFactory: IImage.Factory,
    private val hostContainerPath: String,
    private val process: ICloudProcess,
    private val version: IProcessVersion,
    private val serverJar: File,
) {

    private val tmpDir = File("tmp/${this.process.getName()}")

    private val imageName = DEFAULT_IMAGE_PREFIX + version.getJavBaseImageName()

    fun startProcess(): CompletableFuture<Unit> {
        val image = await(getOrCreateDefaultImageForVersion())
        await(createStartFile())
        copyServerJarInTmpDir()
        val container = createContainer(image)
        container.start()
        return unitFuture()
    }

    private fun createContainer(image: IImage): IContainer {
        val hostRunDir = File(this.hostContainerPath)
        val tmpDirOnHost = File(hostRunDir, tmpDir.path)
        return this.containerFactory.create(
            process.getName(),
            image,
            ContainerSpec()
                .withPortBinding(process.getAddress().port, 25565)
                .withMaxMemory(process.getMaxMemory())
                .withBindVolume(tmpDirOnHost.absolutePath, "/app/")
        )
    }

    private fun createStartFile(): CompletableFuture<Unit> {
        val startFile = File(this.tmpDir, "start.sh")
        startFile.writeText(getStartFileContent())
        startFile.setExecutable(true)
        return unitFuture()
    }

    private fun getStartFileContent(): String {
        return "java ${getJvmArgumentsAsString()}-jar server.jar"
    }

    private fun getJvmArgumentsAsString(): String {
        val jvmArgs = await(getJvmArgumentsOrEmptyObject())
        return if (jvmArgs.getArguments().isEmpty())
            ""
        else
            jvmArgs.getArguments().joinToString(" ") + " "
    }

    private fun getJvmArgumentsOrEmptyObject(): CompletableFuture<IJVMArguments> {
        try {
            val jvmArgs = await(this.process.getJvmArguments())
            return completedFuture(jvmArgs)
        } catch (e: Exception) {
            return completedFuture(IJVMArguments.EMPTY)
        }
    }

    private fun copyServerJarInTmpDir() {
        val dest = File(this.tmpDir, "server.jar")
        FileUtils.copyFile(this.serverJar, dest)
    }


    private fun getOrCreateDefaultImageForVersion(): CompletableFuture<IImage> {
        try {
            val result = await(this.imageRepository.getImageByName(this.imageName))
            return completedFuture(result)
        } catch (ex: Exception) {
            return completedFuture(createDefaultImageForVersion())
        }
    }

    private fun createDefaultImageForVersion(): IImage {
        val tmpDir = File("/tmp/sc-${UUID.randomUUID()}")
        tmpDir.mkdirs()
        return this.imageFactory.create(
            this.imageName,
            tmpDir,
            ImageBuildInstructions()
                .from(version.getJavBaseImageName())
                .expose(25565)
                .workdir("/app/")
                .cmd("/bin/sh", "start.sh")
        )
    }

    companion object {
        const val DEFAULT_IMAGE_PREFIX = "simplecloud-default-"
    }

}