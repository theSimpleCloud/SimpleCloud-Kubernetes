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

import com.ea.async.Async
import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.storagebackend.IStorageBackend
import eu.thesimplecloud.simplecloud.storagebackend.sync.SynchronizationDirection
import eu.thesimplecloud.simplecloud.task.Task
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture

class TemplateCopyTask(
    private val template: ITemplate,
    private val targetDir: File
) : Task<Unit>() {

    override fun getName(): String {
        return "copy_template"
    }

    override fun run(): CompletableFuture<Unit> {
        this.targetDir.mkdirs()
        val templates = await(getSubTemplatesIncludingSelf(this.template))
        templates.forEach { copyTemplateContent(it) }
        return unitFuture()
    }

    private fun copyTemplateContent(template: ITemplate) {
        val templateDir = getDirectoryTemplate(template)
        FileUtils.copyDirectory(templateDir, this.targetDir)
    }

    private fun getDirectoryTemplate(template: ITemplate): File {
        return File("templates/${template.getName()}")
    }

    private fun getSubTemplatesIncludingSelf(template: ITemplate): CompletableFuture<Collection<ITemplate>> {
        if (!template.hasParent()) return completedFuture(listOf(template))
        val parentTemplate = await(template.getParentTemplate())
        val subTemplatesIncludingParent = await(getSubTemplatesIncludingSelf(parentTemplate))
        val collection = subTemplatesIncludingParent.union(listOf(template))
        return completedFuture(collection)
    }

}