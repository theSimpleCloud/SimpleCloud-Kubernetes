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

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Date: 02.09.22
 * Time: 09:51
 * @author Frederick Baier
 *
 */
class YamlFileLoader<T>(
    private val targetFile: File,
    private val pathToLoad: String,
    private val targetClass: Class<T>,
) {

    private val yaml = Yaml(Constructor(this.targetClass))

    fun load(): T {
        try {
            return loadContentFromJar0()
        } catch (ex: Exception) {
            throw YmlFileLoadException(this.targetFile.path, ex)
        }
    }

    private fun loadContentFromJar0(): T {
        val jar = JarFile(this.targetFile)
        val entry: JarEntry = jar.getJarEntry(this.pathToLoad)
            ?: throw YmlFileLoadException("${this.targetFile.path}: No '${this.pathToLoad}' found.")
        val fileStream = jar.getInputStream(entry)
        val loadedObj = this.yaml.load<T>(fileStream)
        jar.close()
        return loadedObj
    }

    class YmlFileLoadException(msg: String, cause: Exception? = null) : Exception(msg, cause)

}