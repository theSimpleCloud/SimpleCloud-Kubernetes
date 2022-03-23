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

package app.simplecloud.simplecloud.api.validator

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.service.ProcessOnlineCountService
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 03/07/2021
 * Time: 13:51
 * @author Frederick Baier
 */
@Singleton
class GroupConfigurationValidator @Inject constructor(
    private val onlineCountService: ProcessOnlineCountService
) : Validator<AbstractCloudProcessGroupConfiguration> {

    override fun validate(value: AbstractCloudProcessGroupConfiguration): CompletableFuture<Unit> = CloudScope.future {
        checkProcessOnlineCountConfiguration(value)
        checkImage(value)
    }

    private suspend fun checkProcessOnlineCountConfiguration(configuration: AbstractCloudProcessGroupConfiguration) {
        val onlineCountConfigurationName = configuration.onlineCountConfigurationName
        try {
            this.onlineCountService.findByName(onlineCountConfigurationName).await()
        } catch (e: Exception) {
            throw NoSuchElementException("OnlineCountConfiguration '${onlineCountConfigurationName}' does not exist")
        }
    }

    private fun checkImage(configuration: AbstractCloudProcessGroupConfiguration) {
        //TODO Validate Image
    }

}