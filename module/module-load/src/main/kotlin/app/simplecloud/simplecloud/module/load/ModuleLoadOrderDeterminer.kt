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

import app.simplecloud.simplecloud.graph.Graph
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContent

/**
 * Date: 12.09.22
 * Time: 18:27
 * @author Frederick Baier
 *
 */
class ModuleLoadOrderDeterminer(
    private val modulesToLoad: List<LoadedModuleFileContent>,
    private val alreadyLoadedModules: List<LoadedModule>,
) {

    private val graph = Graph<String>(allowCycles = false)

    fun determineLoadOrder(): List<LoadedModuleFileContent> {
        addModulesToLoadWithDependenciesToGraph()
        return determineLoadOrderFromGraph()
    }

    private fun determineLoadOrderFromGraph(): List<LoadedModuleFileContent> {
        val orderedModuleNames = graph.getAllOrderedBySuccessorFirst()
        val unloadedOrderedModules = orderedModuleNames.filter { !isModuleLoaded(it) }
        return unloadedOrderedModules.mapNotNull { getModuleToLoadByName(it) }
    }

    private fun addModulesToLoadWithDependenciesToGraph() {
        for (loadedFileContent in this.modulesToLoad) {
            addModuleWithDependenciesToGraph(loadedFileContent)
        }
    }

    private fun addModuleWithDependenciesToGraph(loadedFileContent: LoadedModuleFileContent) {
        val fileContent = loadedFileContent.moduleFileContent
        graph.setNodeWithSuccessors(fileContent.name, getHardAndAvailableSoftDependencies(fileContent))
    }

    private fun getHardAndAvailableSoftDependencies(module: ModuleFileContent): Set<String> {
        val availableSoftDependencies = module.softDepend.filter { isModuleAvailable(it) }
        val hardDependencies = module.depend
        return availableSoftDependencies.union(hardDependencies.toList())
    }

    private fun isModuleAvailable(moduleName: String): Boolean {
        return getAvailableModules().map { it.name }.contains(moduleName)
    }

    //available are modules that already loaded or about to be loaded
    private fun getAvailableModules(): Set<ModuleFileContent> {
        val alreadyLoadedModules = this.alreadyLoadedModules.map { it.fileContent }
        val modulesToLoad = this.modulesToLoad.map { it.moduleFileContent }
        return alreadyLoadedModules.union(modulesToLoad)
    }

    private fun isModuleLoaded(moduleName: String): Boolean {
        return this.alreadyLoadedModules.map { it.fileContent.name.lowercase() }
            .contains(moduleName.lowercase())
    }

    private fun getModuleToLoadByName(moduleName: String): LoadedModuleFileContent? {
        return this.modulesToLoad.firstOrNull { it.moduleFileContent.name.lowercase() == moduleName.lowercase() }
    }

}