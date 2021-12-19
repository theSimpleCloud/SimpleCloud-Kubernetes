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

package eu.thesimplecloud.simplecloud.api.impl.template

import eu.thesimplecloud.simplecloud.api.service.TemplateService
import eu.thesimplecloud.simplecloud.api.template.Template
import eu.thesimplecloud.simplecloud.api.template.configuration.TemplateConfiguration
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 11:55
 * @author Frederick Baier
 */
class TemplateImpl(
    private val configuration: TemplateConfiguration,
    private val templateService: TemplateService
) : Template {

    override fun getParentTemplate(): CompletableFuture<Template> {
        if (!hasParent()) {
            return CompletableFuture.failedFuture(NoSuchElementException("This template has no parent"))
        }
        return this.templateService.findByName(this.configuration.parentTemplateName!!)
    }

    override fun hasParent(): Boolean {
        return this.configuration.parentTemplateName != null
    }

    override fun getName(): String {
        return this.configuration.name
    }

    override fun getIdentifier(): String {
        return getName()
    }

    override fun toConfiguration(): TemplateConfiguration {
        return this.configuration
    }

}