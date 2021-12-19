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
import eu.thesimplecloud.simplecloud.api.future.cloud.nonNull
import eu.thesimplecloud.simplecloud.api.impl.request.template.TemplateCreateRequestImpl
import eu.thesimplecloud.simplecloud.api.impl.request.template.TemplateDeleteRequestImpl
import eu.thesimplecloud.simplecloud.api.impl.template.TemplateImpl
import eu.thesimplecloud.simplecloud.api.internal.service.InternalTemplateService
import eu.thesimplecloud.simplecloud.api.request.template.TemplateCreateRequest
import eu.thesimplecloud.simplecloud.api.request.template.TemplateDeleteRequest
import eu.thesimplecloud.simplecloud.api.template.Template
import eu.thesimplecloud.simplecloud.api.template.configuration.TemplateConfiguration
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import eu.thesimplecloud.simplecloud.api.validator.ValidatorService
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
    private val validatorService: ValidatorService
) : InternalTemplateService {

    private val nameToTemplate = ConcurrentHashMap<String, eu.thesimplecloud.simplecloud.api.template.Template>()

    private val validator = this.validatorService.getValidator(TemplateConfiguration::class.java)

    init {
        this.nameToTemplate["Lobby"] = TemplateImpl(TemplateConfiguration("Lobby", null), this)
    }

    override fun findByName(name: String): CompletableFuture<eu.thesimplecloud.simplecloud.api.template.Template> {
        return CloudCompletableFuture.supplyAsync {
            this.nameToTemplate[name] ?: throw NoSuchElementException("Template '${name}' does not exist")
        }.nonNull()
    }

    override fun findAll(): CompletableFuture<List<Template>> {
        return CloudCompletableFuture.completedFuture(this.nameToTemplate.values.toList())
    }

    override fun createTemplateInternal(configuration: TemplateConfiguration): CompletableFuture<Template> {
        await(this.validator.validate(configuration))
        val template = TemplateImpl(configuration, this)
        this.nameToTemplate[template.getName()] = template
        return CloudCompletableFuture.completedFuture(template)
    }

    override fun deleteTemplateInternal(template: Template) {
        this.nameToTemplate.remove(template.getName())
    }

    override fun createTemplateCreateRequest(configuration: TemplateConfiguration): TemplateCreateRequest {
        return TemplateCreateRequestImpl(this, configuration)
    }

    override fun createTemplateDeleteRequest(template: Template): TemplateDeleteRequest {
        return TemplateDeleteRequestImpl(this, template)
    }
}