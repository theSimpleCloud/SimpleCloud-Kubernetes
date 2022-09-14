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

import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContent
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContentLoader
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContentLoaderImpl
import app.simplecloud.simplecloud.module.load.util.ModuleClassFinder
import app.simplecloud.simplecloud.module.load.util.ModuleJarProvider
import app.simplecloud.simplecloud.module.load.util.TmpDirProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Date: 02.09.22
 * Time: 10:17
 * @author Frederick Baier
 *
 */
class UnsafeModuleLoaderTest {

    private val moduleFileContentLoader: ModuleFileContentLoader = ModuleFileContentLoaderImpl()

    private lateinit var fileToLoad: File
    private lateinit var moduleFileContent: ModuleFileContent

    @BeforeEach
    fun setUp() {
        this.fileToLoad = ModuleJarProvider.emptyJar("TestModule")
        this.moduleFileContent = this.moduleFileContentLoader.load(this.fileToLoad)
    }

    @AfterEach
    fun tearDown() {
        TmpDirProvider.cleanUp()
    }

    @Test
    fun test() {
        val unsafeModuleLoader: UnsafeModuleLoader = UnsafeModuleLoaderImpl()
        val loadedModule = unsafeModuleLoader.load(NotFoundModuleClassFinder(), this.fileToLoad, this.moduleFileContent)
        Assertions.assertEquals(this.fileToLoad, loadedModule.file)
        Assertions.assertEquals(this.moduleFileContent, loadedModule.fileContent)
    }

    class NotFoundModuleClassFinder : ModuleClassFinder {
        override fun findModuleClass(name: String): Class<*> {
            throw ClassNotFoundException("NotFoundModuleClassFinder: $name")
        }

    }

}