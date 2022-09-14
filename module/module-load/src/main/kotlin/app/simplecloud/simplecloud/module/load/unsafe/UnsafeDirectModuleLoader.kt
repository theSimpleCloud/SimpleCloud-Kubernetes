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

package app.simplecloud.simplecloud.module.load.unsafe

import app.simplecloud.simplecloud.module.api.CloudModule
import app.simplecloud.simplecloud.module.load.LoadedModule
import app.simplecloud.simplecloud.module.load.classloader.ModuleClassLoader
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContent
import app.simplecloud.simplecloud.module.load.util.ModuleClassFinder
import java.io.File

/**
 * Date: 02.09.22
 * Time: 10:39
 * @author Frederick Baier
 *
 */
class UnsafeDirectModuleLoader(
    private val file: File,
    private val moduleFileContent: ModuleFileContent,
    private val parentClassLoader: ClassLoader,
    private val moduleClassFinder: ModuleClassFinder,
) {

    private val classLoader = createClassLoader()

    fun load(): LoadedModule {
        val cloudModule = loadModuleClassInstance()
        return LoadedModule(cloudModule, this.file, this.moduleFileContent, this.classLoader)
    }

    private fun loadModuleClassInstance(): CloudModule {
        val mainClass = loadModuleClass()
        val constructor = mainClass.getConstructor()
        return constructor.newInstance()
    }

    private fun loadModuleClass(): Class<out CloudModule> {
        val mainClass = this.classLoader.loadClass(this.moduleFileContent.main)
        return mainClass.asSubclass(CloudModule::class.java)
    }

    private fun createClassLoader(): ModuleClassLoader {
        return ModuleClassLoader(arrayOf(this.file.toURI().toURL()), this.parentClassLoader, this.moduleClassFinder)
    }

}