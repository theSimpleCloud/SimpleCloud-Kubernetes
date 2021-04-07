package eu.thesimplecloud.application.loader

import eu.thesimplecloud.application.ILoadedApplication
import eu.thesimplecloud.application.data.IApplicationData
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 26.03.2021
 * Time: 21:17
 */
abstract class AbstractApplicationLoader<T : ILoadedApplication<*>>(
    private val jarFileLoader: AbstractJarFileLoader<*>
) {

    fun loadAllApplications(directory: File): List<T> {
        require(directory.isDirectory)
        return directory.listFiles()?.map {
            val applicationData = jarFileLoader.loadJsonFileInJar(it)
            loadApplication(it, applicationData)
        } ?: emptyList()
    }

    abstract fun loadApplication(file: File, applicationData: IApplicationData): T

    abstract fun createModuleClassLoader(file: File): ClassLoader

}