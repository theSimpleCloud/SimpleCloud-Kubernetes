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

import app.simplecloud.simplecloud.module.load.exception.ModuleLoadException
import app.simplecloud.simplecloud.module.load.unsafe.UnsafeModuleLoader
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 05.09.22
 * Time: 09:14
 * @author Frederick Baier
 *
 */
class ModuleListLoader(
    private val modulesToLoad: List<LoadedModuleFileContent>,
    private val alreadyLoadedModules: List<LoadedModule>,
    private val unsafeModuleLoader: UnsafeModuleLoader,
    private val errorHandler: (Throwable) -> Unit = { throw it },
) {

    private val loadedModules = CopyOnWriteArrayList<LoadedModule>()

    fun load(): List<LoadedModule> {
        return this.modulesToLoad.mapNotNull { loadModuleCatching(it) }
    }

    private fun loadModuleCatching(fileContent: LoadedModuleFileContent): LoadedModule? {
        try {
            return loadModule(fileContent)
        } catch (ex: Exception) {
            this.errorHandler.invoke(ex)
            return null
        }
    }

    private fun loadModule(loadedFileContent: LoadedModuleFileContent): LoadedModule {
        if (isModuleLoaded(loadedFileContent))
            throw ModuleLoadException("Module '${loadedFileContent.moduleFileContent.name}' is already loaded")
        return loadModuleUnsafe(loadedFileContent)
    }

    private fun loadModuleUnsafe(loadedFileContent: LoadedModuleFileContent): LoadedModule {
        val loadedModule = this.unsafeModuleLoader.load(loadedFileContent.file, loadedFileContent.moduleFileContent)
        this.loadedModules.add(loadedModule)
        return loadedModule
    }

    private fun isModuleLoaded(loadedFileContent: LoadedModuleFileContent): Boolean {
        return getAllLoadedModules().map { it.fileContent.name.lowercase() }
            .contains(loadedFileContent.moduleFileContent.name.lowercase())
    }

    private fun getAllLoadedModules(): Collection<LoadedModule> {
        return this.alreadyLoadedModules.union(this.loadedModules)
    }

}