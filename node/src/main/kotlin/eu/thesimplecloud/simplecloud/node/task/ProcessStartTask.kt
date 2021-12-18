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
import com.google.inject.Injector
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.container.container.IContainer
import eu.thesimplecloud.simplecloud.container.image.IImage
import eu.thesimplecloud.simplecloud.node.process.container.IContainerProcessStarter
import eu.thesimplecloud.simplecloud.task.Task
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture

class ProcessStartTask(
    private val process: ICloudProcess,
    private val containerFactory: IContainer.Factory,
    private val imageFactory: IImage.Factory,
    private val processStarter: IContainerProcessStarter,
    private val injector: Injector
) : Task<Unit>() {

    private val tmpDir = File("tmp/${process.getName()}")

    override fun getName(): String {
        return "start_process"
    }

    override fun run(): CompletableFuture<Unit> {
        val template = await(process.getTemplate())
        val version = await(process.getVersion())
        await(copyTemplates(template))
        copyPluginIntoPluginsFolder()
        await(createSimpleCloudFile())
        val serverJar = await(providerServerJar(version))
        await(this.processStarter.startProcess(this.process, serverJar))
        return unitFuture()
    }

    private fun copyPluginIntoPluginsFolder() {
        val tmpPluginFile = File(this.tmpDir, "plugins/SimpleCloud-Plugin.jar")
        FileUtils.copyFile(PLUGIN_FILE, tmpPluginFile)
    }

    private fun providerServerJar(version: IProcessVersion): CompletableFuture<File> {
        val processVersionProvisionTask = ProcessVersionProvisionTask(version, containerFactory, imageFactory)
        return this.taskSubmitter.submit(processVersionProvisionTask)
    }

    private fun copyTemplates(template: ITemplate): CompletableFuture<Unit> {
        val templateCopyTask = TemplateCopyTask(template, this.tmpDir)
        return this.taskSubmitter.submit(templateCopyTask)
    }

    private fun createSimpleCloudFile(): CompletableFuture<Unit> {
        val simpleCloudFileCreationTask = SimpleCloudFileCreationTask(this.tmpDir, this.process, this.injector)
        return this.taskSubmitter.submit(simpleCloudFileCreationTask)
    }

    companion object {
        val PLUGIN_FILE = File("/node-image/SimpleCloud-Plugin.jar")
    }
}