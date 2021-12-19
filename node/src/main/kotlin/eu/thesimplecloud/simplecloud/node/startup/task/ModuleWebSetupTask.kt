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

package eu.thesimplecloud.simplecloud.node.startup.task

import com.ea.async.Async.await
import eu.thesimplecloud.simplecloud.api.module.ModuleType
import eu.thesimplecloud.simplecloud.node.repository.ModuleRepository
import eu.thesimplecloud.simplecloud.node.repository.ModuleEntity
import eu.thesimplecloud.simplecloud.restserver.setup.RestSetupManager
import eu.thesimplecloud.simplecloud.restserver.setup.body.ModuleSetupResponseBody
import eu.thesimplecloud.simplecloud.restserver.setup.type.Setup
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 08/08/2021
 * Time: 09:52
 * @author Frederick Baier
 */
class ModuleWebSetupTask(
    private val setupManager: RestSetupManager,
    private val moduleRepository: ModuleRepository,
    private val moduleType: ModuleType
) {

    fun run(): CompletableFuture<Unit> {
        return waitForModuleSetup(moduleType)
    }

    private fun waitForModuleSetup(moduleType: ModuleType): CompletableFuture<Unit> {
        val setupName = "module/${moduleType.name.lowercase().replace("_", "")}"
        val setupFuture = this.setupManager.setNextSetup(Setup(setupName, emptyArray<String>(), ModuleSetupResponseBody::class))
        val response = await(setupFuture)
        return this.moduleRepository.save(moduleType, ModuleEntity(moduleType, response.downloadURL))
    }

}