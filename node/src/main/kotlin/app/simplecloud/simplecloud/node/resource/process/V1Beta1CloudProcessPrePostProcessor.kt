/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.node.resource.process

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.StaticProcessTemplateService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionRequestPrePostProcessor
import app.simplecloud.simplecloud.node.process.InternalProcessShutdownHandler
import app.simplecloud.simplecloud.node.process.ProcessShutdownHandler
import app.simplecloud.simplecloud.node.process.ProcessStarter
import kotlinx.coroutines.runBlocking

/**
 * Date: 16.03.23
 * Time: 08:59
 * @author Frederick Baier
 *
 */
class V1Beta1CloudProcessPrePostProcessor(
    private val groupService: CloudProcessGroupService,
    private val staticService: StaticProcessTemplateService,
    private val processService: CloudProcessService,
    private val processStarterFactory: ProcessStarter.Factory,
    private val processShutdownHandlerFactory: ProcessShutdownHandler.Factory,
) : ResourceVersionRequestPrePostProcessor<V1Beta1CloudProcessSpec>() {

    override fun preCreate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: V1Beta1CloudProcessSpec,
    ): RequestPreProcessorResult<V1Beta1CloudProcessSpec> = runBlocking {
        val editedSpec = V1Beta1ProcessCreateHandler(
            name,
            spec,
            groupService,
            staticService,
            processService,
            processStarterFactory
        ).handleCreate()
        return@runBlocking RequestPreProcessorResult.overwriteSpec(editedSpec)
    }

    override fun preDelete(
        group: String,
        version: String,
        kind: String,
        name: String,
    ): RequestPreProcessorResult<Any> = runBlocking {
        try {
            val cloudProcess = processService.findByName(name).await()
            InternalProcessShutdownHandler(cloudProcess, processShutdownHandlerFactory)
                .shutdownProcess()
            return@runBlocking RequestPreProcessorResult.blockSilently()
        } catch (e: NoSuchElementException) {
            return@runBlocking RequestPreProcessorResult.continueNormally()
        }
    }

}