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

package eu.thesimplecloud.simplecloud.api.impl.request.template

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.isCompletedNormally
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalTemplateService
import eu.thesimplecloud.simplecloud.api.request.template.ITemplateCreateRequest
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.api.template.configuration.TemplateConfiguration
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 12/07/2021
 * Time: 10:10
 * @author Frederick Baier
 */
class TemplateCreateRequest(
    private val templateService: IInternalTemplateService,
    private val configuration: TemplateConfiguration
) : ITemplateCreateRequest {

    override fun submit(): CompletableFuture<ITemplate> {
        if (await(doesTemplateExist(configuration.name))) {
            throw IllegalArgumentException("Template does already exist")
        }
        return this.templateService.createTemplateInternal(this.configuration)
    }

    private fun doesTemplateExist(name: String): CompletableFuture<Boolean> {
        val completableFuture = this.templateService.findByName(name)
        return completableFuture.handle { _, _ -> completableFuture.isCompletedNormally }
    }
}