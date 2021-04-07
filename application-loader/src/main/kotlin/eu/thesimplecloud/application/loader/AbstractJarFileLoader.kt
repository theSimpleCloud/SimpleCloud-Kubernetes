package eu.thesimplecloud.application.loader

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Singleton
import eu.thesimplecloud.application.data.DefaultApplicationData
import eu.thesimplecloud.application.data.IApplicationData
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.04.2021
 * Time: 21:13
 */
abstract class AbstractJarFileLoader<T: IApplicationData> {

    private val objectMapper = ObjectMapper()

    fun loadJsonFileInJar(file: File, path: String): T {
        require(file.exists()) { "Specified file to load $path from does not exist: ${file.path}" }
        try {
            val jar = JarFile(file)
            val entry: JarEntry = jar.getJarEntry(path) ?: throw IllegalStateException("test")
            val fileStream = jar.getInputStream(entry)

            val jsonNode = objectMapper.readTree(fileStream)
            jar.close()

            val defaultApplicationData = constructDefaultApplicationData(jsonNode)

            return constructApplicationData(defaultApplicationData, jsonNode)
        } catch (ex: Exception) {
            throw IllegalStateException("test")
        }
    }

    private fun constructDefaultApplicationData(jsonNode: JsonNode): DefaultApplicationData {
        return DefaultApplicationData.fromJsonNode(jsonNode)
    }

    abstract fun constructApplicationData(defaultApplicationData: DefaultApplicationData, jsonNode: JsonNode): T

    abstract fun loadJsonFileInJar(file: File): IApplicationData

}