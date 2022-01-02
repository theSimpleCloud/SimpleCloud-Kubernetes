package app.simplecloud.simplecloud.application.loader

import app.simplecloud.simplecloud.application.LoadedApplication
import app.simplecloud.simplecloud.application.filecontent.ApplicationFileContent
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 26.03.2021
 * Time: 21:17
 */
abstract class AbstractApplicationLoader<T : LoadedApplication>(
    private val jarFileLoader: AbstractJarFileLoader<*>
) {

    fun loadAllApplications(directory: File): List<T> {
        require(directory.isDirectory)
        return directory.listFiles()?.map {
            loadApplication(it)
        } ?: emptyList()
    }

    fun loadApplication(file: File): T {
        val applicationData = jarFileLoader.loadJsonFileInJar(file)
        return loadApplication(file, applicationData)
    }

    abstract fun loadApplication(file: File, fileContent: ApplicationFileContent): T

    abstract fun createApplicationClassLoader(file: File): ClassLoader

}