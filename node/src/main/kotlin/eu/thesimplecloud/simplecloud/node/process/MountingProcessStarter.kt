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

package eu.thesimplecloud.simplecloud.node.process

import com.ea.async.Async.await
import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.container.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.CompletableFuture

@Singleton
class MountingProcessStarter @Inject constructor(
    private val containerFactory: IContainer.Factory,
    private val imageFactory: IImage.Factory,
    private val imageRepository: IImageRepository
) : IProcessStarter {

    private val hostContainerPathFile = File("hostContainerPath.txt")
    private val hostContainerPath: String

    init {
        if (!this.hostContainerPathFile.exists())
            throw FileNotFoundException("File 'hostContainerPath.txt' does not exist")
        this.hostContainerPath = this.hostContainerPathFile.readText()
    }

    override fun startProcess(process: ICloudProcess, serverJar: File): CompletableFuture<Unit> {
        val version = await(process.getVersion())
        val singleMountingProcessStarter = SingleMountingProcessStarter(
            this.imageRepository,
            this.containerFactory,
            this.imageFactory,
            this.hostContainerPath,
            process,
            version,
            serverJar
        )
        await(singleMountingProcessStarter.startProcess())
        return unitFuture()
    }

}