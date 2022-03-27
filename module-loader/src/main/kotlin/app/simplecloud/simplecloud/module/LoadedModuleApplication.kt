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

package app.simplecloud.simplecloud.module

import app.simplecloud.simplecloud.application.LoadedApplication
import app.simplecloud.simplecloud.application.filecontent.ApplicationFileContent
import com.google.inject.AbstractModule
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.04.2021
 * Time: 22:09
 */
class LoadedModuleApplication(
    private val file: File,
    private val fileContent: ApplicationFileContent,
    private val loadedClassInstance: AbstractModule
) : LoadedApplication {

    override fun getFile(): File {
        return file
    }

    override fun getApplicationFileContent(): ApplicationFileContent {
        return fileContent
    }

    override fun getLoadedClassInstance(): AbstractModule {
        return loadedClassInstance
    }

}