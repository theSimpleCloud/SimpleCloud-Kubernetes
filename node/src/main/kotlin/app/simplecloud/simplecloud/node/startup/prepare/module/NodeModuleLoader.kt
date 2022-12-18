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

package app.simplecloud.simplecloud.node.startup.prepare.module

import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.module.api.impl.ClusterAPIImpl
import app.simplecloud.simplecloud.module.api.impl.LocalAPIImpl
import app.simplecloud.simplecloud.module.api.internal.InternalNodeCloudAPI
import app.simplecloud.simplecloud.module.load.ModuleHandler
import app.simplecloud.simplecloud.module.load.ModuleHandlerImpl
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContentLoader
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContentLoaderImpl
import app.simplecloud.simplecloud.module.load.unsafe.UnsafeModuleLoader
import app.simplecloud.simplecloud.module.load.unsafe.UnsafeModuleLoaderImpl
import app.simplecloud.simplecloud.node.connect.LocalModuleSchedulerWatcher
import java.io.File

class NodeModuleLoader(
    private val localAPI: LocalAPIImpl,
) {
    private val errorCreateHandler = ErrorCreateHandlerImpl()
    private val moduleHandler: ModuleHandler

    init {
        val moduleFileContentLoader: ModuleFileContentLoader = ModuleFileContentLoaderImpl()
        val unsafeModuleLoader: UnsafeModuleLoader = UnsafeModuleLoaderImpl()
        this.moduleHandler =
            ModuleHandlerImpl(
                localAPI,
                moduleFileContentLoader,
                unsafeModuleLoader,
                ModuleErrorHandler(this.errorCreateHandler)
            )

    }

    fun loadModules() {
        val moduleFiles = collectModuleFiles()
        this.moduleHandler.load(moduleFiles)
    }

    fun onClusterActive(internalNodeCloudAPI: InternalNodeCloudAPI) {
        this.errorCreateHandler.setErrorService(internalNodeCloudAPI.getErrorService())
        val clusterAPI = ClusterAPIImpl(internalNodeCloudAPI)
        this.moduleHandler.onClusterActive(clusterAPI)
    }

    fun startModuleSchedulerWatcher(distribution: Distribution) {
        LocalModuleSchedulerWatcher(distribution, this.localAPI.getLocalExecutorService())
            .start()
    }

    fun getModuleClassLoader(): ClassLoader {
        return this.moduleHandler.getModuleClassLoader()
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
