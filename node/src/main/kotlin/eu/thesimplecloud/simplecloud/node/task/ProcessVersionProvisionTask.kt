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
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.process.version.ProcessVersionLoadType
import eu.thesimplecloud.simplecloud.container.container.IContainer
import eu.thesimplecloud.simplecloud.container.image.IImage
import eu.thesimplecloud.simplecloud.node.util.Downloader
import eu.thesimplecloud.simplecloud.task.Task
import java.io.File
import java.util.concurrent.CompletableFuture

class ProcessVersionProvisionTask(
    private val processVersion: IProcessVersion,
    private val containerFactory: IContainer.Factory,
    private val imageFactory: IImage.Factory
) : Task<File>() {

    private val versionsDir = File("processVersions/")
    private val processVersionFile = File(versionsDir, processVersion.getName() + ".jar")

    override fun getName(): String {
        return "process_version_provision_${processVersion.getName()}"
    }

    override fun run(): CompletableFuture<File> {
        if (!isJarProvided())
            await(provideJar())
        return completedFuture(this.processVersionFile)
    }

    private fun isJarProvided(): Boolean {
        return this.processVersionFile.exists()
    }

    private fun provideJar(): CompletableFuture<Unit> {
        return if (this.processVersion.getLoadType() == ProcessVersionLoadType.PAPERCLIP) {
            PaperclipJarProvider(this.processVersion, this.containerFactory, this.imageFactory, this.processVersionFile)
                .provideJar()
        } else {
            downloadJar()
            return unitFuture()
        }

    }

    private fun downloadJar() {
        Downloader.userAgentDownload(this.processVersion.getDownloadLink(), this.processVersionFile)
    }


}