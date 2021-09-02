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
import eu.thesimplecloud.simplecloud.api.future.exception.CompletedWithNullException
import eu.thesimplecloud.simplecloud.api.future.unitFuture
import eu.thesimplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.service.IJvmArgumentsService
import eu.thesimplecloud.simplecloud.api.service.IProcessOnlineCountService
import eu.thesimplecloud.simplecloud.api.service.IProcessVersionService
import eu.thesimplecloud.simplecloud.api.service.ITemplateService
import eu.thesimplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import eu.thesimplecloud.simplecloud.api.utils.future.FutureOriginException
import java.lang.Exception
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

    override fun validate(value: AbstractCloudProcessGroupConfiguration): CompletableFuture<Unit> {
        return CloudCompletableFuture.runAsync {
            await(checkJvmArguments(value))
            await(checkProcessOnlineCountConfiguration(value))
            await(checkTemplate(value))
            await(checkVersion(value))
        }
    }

    private fun checkJvmArguments(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<Unit> {
        val jvmArgumentName = configuration.jvmArgumentName ?: return unitFuture()
        try {
            await(this.jvmArgumentsService.findByName(jvmArgumentName))
        } catch (e: CompletedWithNullException) {
            throw NoSuchElementException("JvmArgumentName '${jvmArgumentName}' does not exist")
        }
        return unitFuture()
    }

    private fun checkProcessOnlineCountConfiguration(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<Unit> {
        val onlineCountConfigurationName = configuration.onlineCountConfigurationName
        try {
            await(this.onlineCountService.findByName(onlineCountConfigurationName))
        } catch (e: Exception) {
            throw NoSuchElementException("OnlineCountConfiguration '${onlineCountConfigurationName}' does not exist")
        }
        return unitFuture()
    }

    private fun checkTemplate(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<Unit> {
        val templateName = configuration.templateName
        try {
            await(this.templateService.findByName(templateName))
        } catch (e: Exception) {
            throw NoSuchElementException("Template '${templateName}' does not exist")
        }
        return unitFuture()
    }

    private fun checkVersion(configuration: AbstractCloudProcessGroupConfiguration): CompletableFuture<Unit> {
        val versionName = configuration.versionName
        try {
            await(this.versionService.findByName(versionName))
        } catch (e: Exception) {
            throw NoSuchElementException("Version '${versionName}' does not exist")
        }
        return unitFuture()
    }

}