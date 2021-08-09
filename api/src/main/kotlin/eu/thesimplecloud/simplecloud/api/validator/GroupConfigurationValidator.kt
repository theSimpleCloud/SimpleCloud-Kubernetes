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

package eu.thesimplecloud.simplecloud.api.validator

import com.google.inject.Inject
import com.google.inject.Singleton
import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.future.voidFuture
import eu.thesimplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.service.IJvmArgumentsService
import eu.thesimplecloud.simplecloud.api.service.IProcessOnlineCountService
import eu.thesimplecloud.simplecloud.api.service.IProcessVersionService
import eu.thesimplecloud.simplecloud.api.service.ITemplateService
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 03/07/2021
 * Time: 13:51
 * @author Frederick Baier
 */
@Singleton
class GroupConfigurationValidator @Inject constructor(
    private val jvmArgumentsService: IJvmArgumentsService,
    private val onlineCountService: IProcessOnlineCountService,
    private val templateService: ITemplateService,
    private val versionService: IProcessVersionService,
) : IValidator<AbstractCloudProcessGroupConfiguration> {

    override fun validate(value: AbstractCloudProcessGroupConfiguration): CompletableFuture<Void> {
        return CompletableFuture.supplyAsync {
            checkJvmArguments(value)
            checkProcessOnlineCountConfiguration(value)
            checkTemplate(value)
            checkVersion(value)
            return@supplyAsync null
        }
    }

    private fun checkJvmArguments(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<Void> {
        val jvmArgumentName = configuration.jvmArgumentName ?: return voidFuture()
        await(this.jvmArgumentsService.findByName(jvmArgumentName))
        return voidFuture()
    }

    private fun checkProcessOnlineCountConfiguration(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<Void> {
        await(this.onlineCountService.findByName(configuration.onlineCountConfigurationName))
        return voidFuture()
    }

    private fun checkTemplate(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<Void> {
        await(this.templateService.findByName(configuration.templateName))
        return voidFuture()
    }

    private fun checkVersion(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<Void> {
        await(this.versionService.findByName(configuration.versionName))
        return voidFuture()
    }

}