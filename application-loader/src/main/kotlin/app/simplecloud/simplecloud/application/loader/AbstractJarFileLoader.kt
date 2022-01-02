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