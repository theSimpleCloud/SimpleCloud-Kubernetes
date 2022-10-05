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

package app.simplecloud.simplecloud.node.startup.prepare

import app.simplecloud.simplecloud.api.internal.InternalNodeCloudAPI
import app.simplecloud.simplecloud.module.api.impl.ClusterAPIImpl
import app.simplecloud.simplecloud.module.api.impl.LocalAPIImpl
import app.simplecloud.simplecloud.module.load.ModuleHandler
import app.simplecloud.simplecloud.module.load.ModuleHandlerImpl
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContentLoaderImpl
import app.simplecloud.simplecloud.module.load.unsafe.UnsafeModuleLoader
import app.simplecloud.simplecloud.module.load.unsafe.UnsafeModuleLoaderImpl
import java.io.File

class NodeModuleLoader {

    private val localAPI = LocalAPIImpl()
    private val moduleHandler: ModuleHandler

    init {
        val moduleFileContentLoaderImpl = ModuleFileContentLoaderImpl()
        val unsafeModuleLoader: UnsafeModuleLoader = UnsafeModuleLoaderImpl()
        this.moduleHandler =
            ModuleHandlerImpl(localAPI, moduleFileContentLoaderImpl, unsafeModuleLoader, ModuleErrorHandler())

    }

    fun loadModules() {
        val moduleFiles = collectModuleFiles()
        this.moduleHandler.load(moduleFiles)
    }

    fun onClusterActive(internalNodeCloudAPI: InternalNodeCloudAPI) {
        val clusterAPI = ClusterAPIImpl(internalNodeCloudAPI)
        this.moduleHandler.onClusterActive(clusterAPI)
    }

    private fun collectModuleFiles(): Set<File> {
        MODULES_DIR.mkdirs()
        val filesInDir = MODULES_DIR.listFiles()!!
        return filesInDir.filter { it.extension == "jar" }.toSet()
    }

    companion object {
        val MODULES_DIR = File("./modules/")
    }

}
