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
import com.google.inject.Guice
import com.google.inject.Injector
import dev.morphia.Datastore
import eu.thesimplecloud.module.LoadedModuleApplication
import eu.thesimplecloud.module.loader.ModuleApplicationLoader
import eu.thesimplecloud.module.loader.ModuleJarFileLoader
import eu.thesimplecloud.simplecloud.api.CloudAPI
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.future.voidFuture
import eu.thesimplecloud.simplecloud.api.module.ModuleType
import eu.thesimplecloud.simplecloud.node.repository.ModuleEntity
import eu.thesimplecloud.simplecloud.node.repository.MongoModuleRepository
import eu.thesimplecloud.simplecloud.node.startup.NodeStartupSetupHandler
import eu.thesimplecloud.simplecloud.node.util.Downloader
import eu.thesimplecloud.simplecloud.task.Task
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 08/08/2021
 * Time: 11:58
 * @author Frederick Baier
 */
class LoadModulesTask(
    private val datastore: Datastore,
    private val nodeSetupHandler: NodeStartupSetupHandler
) : Task<List<LoadedModuleApplication>>() {

    private val moduleRepository = MongoModuleRepository(datastore)
    private val modulesDir = File("modules/")
    private val moduleApplicationLoader = Guice.createInjector().getInstance(ModuleApplicationLoader::class.java)

    init {
        this.modulesDir.mkdirs()
    }

    override fun getName(): String {
        return "load_modules"
    }

    override fun run(): CompletableFuture<List<LoadedModuleApplication>> {
        val loadedModuleApplications = ModuleType.values().map {
            await(loadModuleByType(it))
        }
        return completedFuture(loadedModuleApplications)
    }

    private fun loadModuleByType(moduleType: ModuleType): CompletableFuture<LoadedModuleApplication> {
        val fileByModuleType = getFileByModuleType(moduleType)
        if (doesModuleExistAsFile(moduleType)) {
            return completedFuture(loadModuleByFile(fileByModuleType))
        }
        val moduleEntity = await(this.moduleRepository.findOrNull(moduleType))
        if (moduleEntity != null) {
            Downloader.userAgentDownload(moduleEntity.downloadURL, fileByModuleType)
            return loadModuleByType(moduleType)
        }
        await(executeModuleSetupInWeb(moduleType))
        return loadModuleByType(moduleType)
    }

    private fun loadModuleByFile(fileByModuleType: File): LoadedModuleApplication {
        return moduleApplicationLoader.loadApplication(fileByModuleType)
    }

    private fun doesModuleExistAsFile(moduleType: ModuleType): Boolean {
        return getFileByModuleType(moduleType).exists()
    }

    private fun getFileByModuleType(moduleType: ModuleType): File {
        return File(modulesDir, moduleType.name + ".jar")
    }

    private fun executeModuleSetupInWeb(moduleType: ModuleType): CompletableFuture<Void> {
        return this.nodeSetupHandler.executeSetupTask(this.taskSubmitter) {
            ModuleWebSetupTask(it, moduleRepository, moduleType)
        }
    }

}