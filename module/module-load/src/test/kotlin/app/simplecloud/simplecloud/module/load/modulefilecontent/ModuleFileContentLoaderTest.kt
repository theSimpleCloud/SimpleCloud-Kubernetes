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

package app.simplecloud.simplecloud.module.load.modulefilecontent

import app.simplecloud.simplecloud.module.load.util.ModuleJarProvider
import app.simplecloud.simplecloud.module.load.util.TmpDirProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Date: 01.09.22
 * Time: 17:12
 * @author Frederick Baier
 *
 */
class ModuleFileContentLoaderTest {

    private var moduleFileContentLoader: ModuleFileContentLoader = ModuleFileContentLoaderImpl()

    private lateinit var jarToRead: File

    @BeforeEach
    fun setUp() {
        this.moduleFileContentLoader = ModuleFileContentLoaderImpl()
        this.jarToRead = ModuleJarProvider.failingJar("TestModule")
    }

    @AfterEach
    fun tearDown() {
        TmpDirProvider.cleanUp()
    }

    @Test
    fun test() {
        val moduleFileContent = this.moduleFileContentLoader.load(jarToRead)

        Assertions.assertEquals(moduleFileContent.name, "TestModule")
        Assertions.assertTrue(moduleFileContent.author.isNotBlank())
        Assertions.assertTrue(moduleFileContent.main.isNotBlank())
        Assertions.assertTrue(moduleFileContent.depend.isEmpty())
        Assertions.assertTrue(moduleFileContent.softDepend.isEmpty())
    }

}