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
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContentLoader
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContentLoaderImpl
import app.simplecloud.simplecloud.module.load.testmodule.OnEnableAwareModuleMain
import app.simplecloud.simplecloud.module.load.unsafe.UnsafeModuleLoader
import app.simplecloud.simplecloud.module.load.unsafe.UnsafeModuleLoaderImpl
import app.simplecloud.simplecloud.module.load.util.EmptyNodeAPI
import app.simplecloud.simplecloud.module.load.util.ModuleJarBuilder
import app.simplecloud.simplecloud.module.load.util.ModuleJarProvider
import app.simplecloud.simplecloud.module.load.util.TmpDirProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Date: 02.09.22
 * Time: 10:54
 * @author Frederick Baier
 *
 */
class ModuleHandlerTest {

    private lateinit var moduleHandler: ModuleHandler
    private lateinit var errorHandler: MyErrorHandler

    @BeforeEach
    fun setUp() {
        this.errorHandler = MyErrorHandler()
        val unsafeModuleLoader: UnsafeModuleLoader = UnsafeModuleLoaderImpl()
        val moduleFileContentLoader: ModuleFileContentLoader = ModuleFileContentLoaderImpl()
        this.moduleHandler =
            ModuleHandlerImpl(EmptyNodeAPI(), moduleFileContentLoader, unsafeModuleLoader, errorHandler)
    }

    @AfterEach
    fun tearDown() {
        TmpDirProvider.cleanUp()
    }

    @Test
    fun loadEmptyList_willNotFail() {
        this.moduleHandler.load(emptySet<File>())
    }

    @Test
    fun loadSingleJar_willBeLoaded() {
        val moduleJar = ModuleJarProvider.emptyJar("TestModule")
        val loadedModules = this.moduleHandler.load(setOf(moduleJar))
        Assertions.assertEquals(1, loadedModules.size)
        val loadedModule = loadedModules[0]
        Assertions.assertEquals("TestModule", loadedModule.fileContent.name)
    }

    @Test
    fun loadTwoJarsWithSameName_willFail() {
        val moduleJar = ModuleJarProvider.emptyJar("TestModule")
        val moduleJar2 = ModuleJarProvider.emptyJar("TestModule")

        assertModuleError(ModuleLoadException::class.java) {
            this.moduleHandler.load(setOf(moduleJar, moduleJar2))
        }
    }

    //load with same name different steps

    @Test
    fun loadSameJarTwice_willFail() {
        val moduleJar = ModuleJarProvider.emptyJar("TestModule")

        this.moduleHandler.load(setOf(moduleJar))
        assertModuleError(ModuleLoadException::class.java) {
            this.moduleHandler.load(setOf(moduleJar))
        }
    }

    @Test
    fun loadJarWithSameNameInDifferentLoadCalls_willFail() {
        val moduleJar = ModuleJarProvider.emptyJar("TestModule")
        val moduleJar2 = ModuleJarProvider.emptyJar("TestModule")

        this.moduleHandler.load(setOf(moduleJar))
        assertModuleError(ModuleLoadException::class.java) {
            this.moduleHandler.load(setOf(moduleJar2))
        }
    }

    @Test
    fun loadOneJar_onEnableWillBeCalled() {
        val moduleJar = ModuleJarProvider.enableAwareJar("TestModule")

        val loadedModules = this.moduleHandler.load(setOf(moduleJar))

        val loadedModule = loadedModules[0]
        assertEnableAwareModuleWasCalled(loadedModule)
    }

    @Test
    fun afterOneError_otherModulesWillLoadNormally() {
        val moduleJar1 = ModuleJarProvider.enableAwareJar("TestModule")
        val moduleJar2 = ModuleJarProvider.enableAwareJar("TestModule")
        val moduleJar3 = ModuleJarProvider.enableAwareJar("TestModule3")

        //second module will fail because of duplicate name
        assertModuleError(ModuleLoadException::class.java) {
            val loadedModules = this.moduleHandler.load(setOf(moduleJar1, moduleJar2, moduleJar3))
            Assertions.assertEquals(2, loadedModules.size)
            loadedModules.forEach {
                assertEnableAwareModuleWasCalled(it)
            }
        }
    }

    @Test
    fun afterOneModuleEnablesWithError_otherModulesWillLoadNormally() {
        val moduleJar1 = ModuleJarProvider.failingJar("TestModule")
        val moduleJar2 = ModuleJarProvider.emptyJar("TestModule2")

        assertModuleError(ModuleLoadException::class.java) {
            val loadedModules = this.moduleHandler.load(setOf(moduleJar1, moduleJar2))
            Assertions.assertTrue(loadedModules.isNotEmpty())
        }
    }

    @Test
    fun loadTwoModules_canFindEachOthersClasses() {
        val moduleJar1 = ModuleJarBuilder("TestModule")
            .mainClass(ModuleJarBuilder.MainClassType.ENABLE_AWARE).build()
        val moduleJar2 = ModuleJarBuilder("TestModule2")
            .mainClass(ModuleJarBuilder.MainClassType.EMPTY).build()

        val loadedModules = this.moduleHandler.load(setOf(moduleJar1, moduleJar2))

        assertLoadedModulesCanAllFindClasses(
            loadedModules,
            listOf(
                ModuleJarBuilder.MainClassType.ENABLE_AWARE.dotString(),
                ModuleJarBuilder.MainClassType.EMPTY.dotString()
            )
        )
    }

    @Test
    fun moduleDependOnOtherModule_willBeLoadedLater() {
        val moduleJar1 = ModuleJarBuilder("TestModule")
            .mainClass(ModuleJarBuilder.MainClassType.ENABLE_AWARE).build()
        val moduleJar2 = ModuleJarBuilder("TestModule2")
            .mainClass(ModuleJarBuilder.MainClassType.ENABLE_AWARE)
            .depends(arrayOf("TestModule")).build()

        val loadedModules = this.moduleHandler.load(setOf(moduleJar1, moduleJar2))

    }

    private fun assertLoadedModulesCanAllFindClasses(loadedModules: List<LoadedModule>, classes: List<String>) {
        for (loadedModule in loadedModules) {
            for (className in classes) {
                Assertions.assertDoesNotThrow {
                    loadedModule.classLoader.loadClass(className)
                }
            }
        }
    }

    private fun assertEnableAwareModuleWasCalled(loadedModule: LoadedModule) {
        val cloudModule = loadedModule.cloudModule as OnEnableAwareModuleMain
        Assertions.assertTrue(cloudModule.wasOnEnableCalled)
    }

    private fun assertModuleError(expectedError: Class<out Throwable>, function: () -> Unit) {
        Assertions.assertNull(this.errorHandler.lastError, "Error Handler caught an error before expecting one")
        function()
        Assertions.assertThrows(expectedError) {
            val error = this.errorHandler.lastError ?: return@assertThrows
            throw error
        }
    }

    class MyErrorHandler() : (Throwable) -> Unit {

        @Volatile
        var lastError: Throwable? = null
            private set

        override fun invoke(error: Throwable) {
            this.lastError = error
        }

    }
}