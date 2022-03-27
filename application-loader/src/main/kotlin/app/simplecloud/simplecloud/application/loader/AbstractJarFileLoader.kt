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

package app.simplecloud.simplecloud.application.loader

import app.simplecloud.simplecloud.application.exception.ApplicationLoadException
import app.simplecloud.simplecloud.application.filecontent.ApplicationFileContent
import app.simplecloud.simplecloud.application.filecontent.DefaultApplicationFileContent
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.04.2021
 * Time: 21:13
 */
abstract class AbstractJarFileLoader<T : ApplicationFileContent>(
    private val pathToEntryFile: String
) {

    private val objectMapper = ObjectMapper()

    fun loadJsonFileInJar(file: File): T {
        require(file.exists()) { "Specified file to load ${this.pathToEntryFile} from does not exist: ${file.path}" }
        val jsonNode = loadEntryFileAsJsonNodeInJarCatching(file)
        val defaultApplicationData = constructDefaultApplicationData(jsonNode)
        return constructCustomApplicationFileContent(defaultApplicationData, jsonNode)
    }

    private fun loadEntryFileAsJsonNodeInJarCatching(file: File): JsonNode {
        try {
            return loadEntryFileAsJsonNodeInJar(file)
        } catch (ex: Exception) {
            throw ApplicationLoadException(file, ex)
        }
    }

    private fun loadEntryFileAsJsonNodeInJar(file: File): JsonNode {
        val jar = JarFile(file)
        val entry: JarEntry = jar.getJarEntry(this.pathToEntryFile) ?: throw IllegalStateException("test")
        val fileStream = jar.getInputStream(entry)

        val jsonNode = objectMapper.readTree(fileStream)
        jar.close()
        return jsonNode
    }

    private fun constructDefaultApplicationData(jsonNode: JsonNode): DefaultApplicationFileContent {
        return DefaultApplicationFileContent.fromJsonNode(jsonNode)
    }

    abstract fun constructCustomApplicationFileContent(
        defaultApplicationData: DefaultApplicationFileContent,
        jsonNode: JsonNode
    ): T

}