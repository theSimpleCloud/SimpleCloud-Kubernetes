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

package app.simplecloud.simplecloud.module.load.util

import java.io.File

/**
 * Date: 01.09.22
 * Time: 17:21
 * @author Frederick Baier
 *
 */
class ModuleJarCreator(
    private val name: String,
    private val pathToMainCass: String,
    private val depends: Array<String>,
    private val softDepends: Array<String>,
) {

    private val tmpDir = TmpDirProvider.generateTmpDir()
    private val targetFile = File(tmpDir, "$name.jar")
    private val jarCreator = JarCreator(targetFile)

    fun createJar(): File {
        jarCreator.createJar()
        addMainClass()
        jarCreator.close()
        return targetFile
    }

    private fun addMainClass() {
        jarCreator.addFile(PATH_BEGIN, PATH_BEGIN + this.pathToMainCass)
        val moduleFile = createModuleContentFileWithMainClass()
        jarCreator.addFile(tmpDir.path, moduleFile.path)
    }

    private fun createModuleContentFileWithMainClass(): File {
        val file = File(tmpDir, "module.yml")
        file.parentFile.mkdirs()
        file.createNewFile()
        file.writeText(getModuleFileContentString())
        return file
    }

    private fun getModuleFileContentString(): String {
        val pathInJar = this.pathToMainCass
        val replacedMain = pathInJar.replace("/", ".").replace(".class", "")
        return """
            name: ${this.name}
            main: $replacedMain
            author: Fllip
            depend: [${getYamlArrayString(this.depends)}]
            softDepend: [${getYamlArrayString(this.softDepends)}]
        """.trimIndent()
    }

    private fun getYamlArrayString(array: Array<String>): String {
        if (array.isEmpty())
            return ""
        return array.joinToString("', '", "'", "'")
    }

    companion object {
        const val PATH_BEGIN = "./build/classes/kotlin/test/"

    }

}