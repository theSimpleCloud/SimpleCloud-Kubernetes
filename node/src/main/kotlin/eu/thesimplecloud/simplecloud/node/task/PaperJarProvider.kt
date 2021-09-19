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

package eu.thesimplecloud.simplecloud.node.task

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.container.ContainerSpec
import eu.thesimplecloud.simplecloud.container.IContainer
import eu.thesimplecloud.simplecloud.container.IImage
import eu.thesimplecloud.simplecloud.container.ImageBuildInstructions
import eu.thesimplecloud.simplecloud.node.util.Downloader
import eu.thesimplecloud.simplecloud.node.util.ZipUtil
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture

class PaperJarProvider(
    private val processVersion: IProcessVersion,
    private val containerFactory: IContainer.Factory,
    private val imageFactory: IImage.Factory,
    private val processVersionFile: File
) {

    private val tmpPaperclipDir = File("paperclip/${processVersion.getName()}")
    private val paperClipFile = File(tmpPaperclipDir, "paperclip.jar")

    fun provideJar(): CompletableFuture<Unit> {
        downloadPaperclip()
        val container = createBuildContainer(tmpPaperclipDir)
        await(executeContainer(container))
        copyProcessVersionOutOfContainer(container, tmpPaperclipDir)
        deleteTmpDir()
        return unitFuture()
    }

    private fun downloadPaperclip() {
        Downloader.userAgentDownload(this.processVersion.getDownloadLink(), this.paperClipFile)
    }

    private fun createBuildContainer(tmpPaperclipDir: File): IContainer {
        val image = this.imageFactory.create(
            "test2",
            tmpPaperclipDir,
            ImageBuildInstructions()
                .from("adoptopenjdk:16.0.1_9-jdk-hotspot")
                .workdir("/app/")
                .copy(".", "/app/")
                .cmd("java", "-jar", "/app/paperclip.jar")
        )
        return containerFactory.create(
            "build_${processVersion.getName()}",
            image,
            ContainerSpec()
                .withMaxMemory(2048)
        )
    }

    private fun executeContainer(container: IContainer): CompletableFuture<Unit> {
        container.start()
        return container.terminationFuture()
    }

    private fun copyProcessVersionOutOfContainer(container: IContainer, tmpPaperclipDir: File) {
        val tarFile = File(tmpPaperclipDir, "cache.tar")
        container.copyFromContainer("/app/cache", tarFile)
        ZipUtil.unzipTar(tarFile, tmpPaperclipDir)
        val patchedFile = getPatchedFile(tmpPaperclipDir)
        FileUtils.copyFile(patchedFile, this.processVersionFile)
    }

    private fun getPatchedFile(tmpPaperclipFile: File): File {
        val dir = File(tmpPaperclipFile, "cache")
        return dir.listFiles().first { it.name.contains("patched") }
    }

    private fun deleteTmpDir() {
        FileUtils.deleteDirectory(this.tmpPaperclipDir)
    }


}
