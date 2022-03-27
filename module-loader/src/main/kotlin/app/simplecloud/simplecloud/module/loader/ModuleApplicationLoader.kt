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

package app.simplecloud.simplecloud.module.loader

import app.simplecloud.simplecloud.application.filecontent.ApplicationFileContent
import app.simplecloud.simplecloud.application.loader.AbstractApplicationLoader
import app.simplecloud.simplecloud.application.loader.ApplicationClassLoader
import app.simplecloud.simplecloud.application.loader.ExtensionLoader
import app.simplecloud.simplecloud.module.LoadedModuleApplication
import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.04.2021
 * Time: 22:03
 */
@Singleton
class ModuleApplicationLoader @Inject constructor(
    private val injector: Injector,
    jarFileLoader: ModuleJarFileLoader
) : AbstractApplicationLoader<LoadedModuleApplication>(jarFileLoader) {

    override fun loadApplication(
        file: File,
        fileContent: ApplicationFileContent
    ): LoadedModuleApplication {
        val classLoader = createApplicationClassLoader(file)
        val classNameToLoad = fileContent.getClassNameToLoad()

        val extensionLoader = ExtensionLoader(injector, classLoader, AbstractModule::class.java)
        val abstractModule = extensionLoader.loadClassInstance(classNameToLoad)

        return LoadedModuleApplication(file, fileContent, abstractModule)
    }

    override fun createApplicationClassLoader(file: File): ClassLoader {
        return ApplicationClassLoader(listOf(file.toURI().toURL()), this::class.java.classLoader)
    }

}
