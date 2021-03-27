package eu.thesimplecloud.application.loader

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import eu.thesimplecloud.application.data.DefaultApplicationData
import eu.thesimplecloud.application.data.IApplicationData
import eu.thesimplecloud.application.LoadedApplication
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 26.03.2021
 * Time: 21:17
 */
abstract class AbstractApplicationLoader<D : IApplicationData>{

    private val objectMapper = ObjectMapper()

    fun loadAllApplications(directory: File): List<LoadedApplication<*>> {
        require(directory.isDirectory)
        return directory.listFiles()?.map{
            val applicationData = loadJsonFileInJar(it)
            loadApplication(it, applicationData)
        }?: emptyList()
    }


    fun loadJsonFileInJar(file: File, path: String): D {
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

    abstract fun loadApplication(file: File, applicationData: D): LoadedApplication<*>

    abstract fun loadJsonFileInJar(file: File): D

    abstract fun constructApplicationData(defaultApplicationData: DefaultApplicationData, jsonNode: JsonNode): D

    abstract fun createModuleClassLoader(file: File): ClassLoader

}