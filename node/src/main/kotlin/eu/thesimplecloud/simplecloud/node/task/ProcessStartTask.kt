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
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.container.IContainer
import eu.thesimplecloud.simplecloud.container.IImage
import eu.thesimplecloud.simplecloud.node.process.IProcessStarter
import eu.thesimplecloud.simplecloud.storagebackend.IStorageBackend
import eu.thesimplecloud.simplecloud.task.Task
import java.io.File
import java.util.concurrent.CompletableFuture

class ProcessStartTask(
    private val process: ICloudProcess,
    private val containerFactory: IContainer.Factory,
    private val imageFactory: IImage.Factory,
    private val processStarter: IProcessStarter
) : Task<Unit>() {

    override fun getName(): String {
        return "start_process"
    }

    override fun run(): CompletableFuture<Unit> {
        val template = await(process.getTemplate())
        val version = await(process.getVersion())
        val templateCopyTask = TemplateCopyTask(template, File("tmp/${process.getName()}"))
        await(this.taskSubmitter.submit(templateCopyTask))
        val processVersionProvisionTask = ProcessVersionProvisionTask(version, containerFactory, imageFactory)
        val serverJar = await(this.taskSubmitter.submit(processVersionProvisionTask))
        await(this.processStarter.startProcess(this.process, serverJar))
        return unitFuture()
    }



}