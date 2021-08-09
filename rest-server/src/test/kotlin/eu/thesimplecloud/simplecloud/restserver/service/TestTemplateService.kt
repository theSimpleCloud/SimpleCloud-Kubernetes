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

package eu.thesimplecloud.simplecloud.restserver.service

import com.ea.async.Async.await
import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.impl.request.template.TemplateCreateRequest
import eu.thesimplecloud.simplecloud.api.impl.request.template.TemplateDeleteRequest
import eu.thesimplecloud.simplecloud.api.impl.template.Template
import eu.thesimplecloud.simplecloud.api.internal.service.IInternalTemplateService
import eu.thesimplecloud.simplecloud.api.request.template.ITemplateCreateRequest
import eu.thesimplecloud.simplecloud.api.request.template.ITemplateDeleteRequest
import eu.thesimplecloud.simplecloud.api.template.ITemplate
import eu.thesimplecloud.simplecloud.api.template.configuration.TemplateConfiguration
import eu.thesimplecloud.simplecloud.api.validator.IValidatorService
import java.util.NoSuchElementException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by IntelliJ IDEA.
 * Date: 03/07/2021
 * Time: 19:07
 * @author Frederick Baier
 */
@Singleton
class TestTemplateService @Inject constructor(
    private val validatorService: IValidatorService
) : IInternalTemplateService {

    private val nameToTemplate = ConcurrentHashMap<String, ITemplate>()

    private val validator = this.validatorService.getValidator(TemplateConfiguration::class.java)

    init {
        this.nameToTemplate["Lobby"] = Template(TemplateConfiguration("Lobby", null), this)
    }

    override fun findByName(name: String): CompletableFuture<ITemplate> {
        return CompletableFuture.supplyAsync {
            this.nameToTemplate[name] ?: throw NoSuchElementException("Template '${name}' does not exist")
        }
    }

    override fun findAll(): CompletableFuture<List<ITemplate>> {
        return CompletableFuture.completedFuture(this.nameToTemplate.values.toList())
    }

    override fun createTemplateInternal(configuration: TemplateConfiguration): CompletableFuture<ITemplate> {
        await(this.validator.validate(configuration))
        val template = Template(configuration, this)
        this.nameToTemplate[template.getName()] = template
        return CompletableFuture.completedFuture(template)
    }

    override fun deleteTemplateInternal(template: ITemplate) {
        this.nameToTemplate.remove(template.getName())
    }

    override fun createTemplateCreateRequest(configuration: TemplateConfiguration): ITemplateCreateRequest {
        return TemplateCreateRequest(this, configuration)
    }

    override fun createTemplateDeleteRequest(template: ITemplate): ITemplateDeleteRequest {
        return TemplateDeleteRequest(this, template)
    }
}