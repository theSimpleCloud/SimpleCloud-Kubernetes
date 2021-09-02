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

package eu.thesimplecloud.simplecloud.api.impl.service

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.impl.request.template.TemplateCreateRequest
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteTemplateRepository
import eu.thesimplecloud.simplecloud.api.impl.request.template.TemplateDeleteRequest
import eu.thesimplecloud.simplecloud.api.impl.template.Template
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalTemplateService
import eu.thesimplecloud.simplecloud.api.request.template.ITemplateCreateRequest
import eu.thesimplecloud.simplecloud.api.request.template.ITemplateDeleteRequest
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.api.template.configuration.TemplateConfiguration
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 20.06.2021
 * Time: 10:38
 * @author Frederick Baier
 */
open class DefaultTemplateService(
    private val igniteRepository: IgniteTemplateRepository
) : IInternalTemplateService {

    override fun findByName(name: String): CompletableFuture<ITemplate> {
        val completableFuture = this.igniteRepository.find(name)
        return completableFuture.thenApply { Template(it, this) }
    }

    override fun findAll(): CompletableFuture<List<ITemplate>> {
        val completableFuture = this.igniteRepository.findAll()
        return completableFuture.thenApply { list -> list.map { Template(it, this) } }
    }

    override fun createTemplateInternal(configuration: TemplateConfiguration): CompletableFuture<ITemplate> {
        this.igniteRepository.save(configuration.name, configuration)
        return completedFuture(Template(configuration, this))
    }

    override fun deleteTemplateInternal(template: ITemplate) {
        this.igniteRepository.remove(template.getIdentifier())
    }

    override fun createTemplateCreateRequest(configuration: TemplateConfiguration): ITemplateCreateRequest {
        return TemplateCreateRequest(this, configuration)
    }

    override fun createTemplateDeleteRequest(template: ITemplate): ITemplateDeleteRequest {
        return TemplateDeleteRequest(this, template)
    }

}