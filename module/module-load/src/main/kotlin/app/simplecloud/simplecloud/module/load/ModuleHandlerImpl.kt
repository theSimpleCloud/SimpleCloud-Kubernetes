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

package app.simplecloud.simplecloud.module.load

import app.simplecloud.simplecloud.module.api.NodeAPI
import app.simplecloud.simplecloud.module.load.exception.ModuleLoadException
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContentLoader
import app.simplecloud.simplecloud.module.load.unsafe.UnsafeModuleLoader
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

class ModuleHandlerImpl(
    private val nodeAPI: NodeAPI,
    private val moduleFileContentLoader: ModuleFileContentLoader,
    private val unsafeModuleLoader: UnsafeModuleLoader,
    private val errorHandler: (Throwable) -> Unit = { throw it },
) : ModuleHandler {

    private val loadedModules = CopyOnWriteArrayList<LoadedModule>()

    override fun load(list: Set<File>): List<LoadedModule> {
        val loadModuleFileContents = loadModuleFileContents(list)
        val newlyLoadedModules = ModuleListLoader(
            loadModuleFileContents,
            this.loadedModules,
            this.unsafeModuleLoader,
            this.errorHandler
        ).load()
        newlyLoadedModules.forEach { enableModuleCatching(it) }
        this.loadedModules.addAll(newlyLoadedModules)
        return newlyLoadedModules
    }

    private fun enableModuleCatching(loadedModule: LoadedModule) {
        try {
            enableModule(loadedModule)
        } catch (ex: Exception) {
            this.errorHandler.invoke(ex)
        }
    }

    private fun enableModule(loadedModule: LoadedModule) {
        try {
            loadedModule.cloudModule.onEnable(this.nodeAPI)
        } catch (ex: Exception) {
            val moduleName = loadedModule.fileContent.name
            throw ModuleLoadException("An error occurred while enabling module '${moduleName}':", ex)
        }
    }

    private fun loadModuleFileContents(list: Set<File>): List<LoadedModuleFileContent> {
        return list.map { loadModuleFileContent(it) }
    }

    private fun loadModuleFileContent(file: File): LoadedModuleFileContent {
        return LoadedModuleFileContent(file, this.moduleFileContentLoader.load(file))
    }


}
