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

package eu.thesimplecloud.simplecloud.node.service

import com.google.inject.Inject
import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteProcessVersionRepository
import eu.thesimplecloud.simplecloud.api.impl.service.DefaultProcessVersionService
import eu.thesimplecloud.simplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.simplecloud.api.process.version.configuration.ProcessVersionConfiguration
import eu.thesimplecloud.simplecloud.node.mongo.processversion.MongoProcessVersionRepository
import eu.thesimplecloud.simplecloud.node.mongo.processversion.ProcessVersionEntity
import java.util.concurrent.CompletableFuture

class ProcessVersionServiceImpl @Inject constructor(
    igniteRepository: IgniteProcessVersionRepository,
    private val mongoRepository: MongoProcessVersionRepository
) : DefaultProcessVersionService(igniteRepository) {

    override fun createProcessVersionInternal(configuration: ProcessVersionConfiguration): CompletableFuture<IProcessVersion> {
        val result =  super.createProcessVersionInternal(configuration)
        saveToDatabase(configuration)
        return result
    }

    private fun saveToDatabase(configuration: ProcessVersionConfiguration) {
        val entity = ProcessVersionEntity(configuration.name, configuration.apiType, configuration.downloadLink)
        this.mongoRepository.save(configuration.name, entity)
    }

    override fun deleteProcessVersionInternal(processVersion: IProcessVersion) {
        super.deleteProcessVersionInternal(processVersion)
        this.mongoRepository.remove(processVersion.getName())
    }

}