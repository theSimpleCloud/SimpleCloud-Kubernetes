package eu.thesimplecloud.module

import com.google.inject.AbstractModule
import eu.thesimplecloud.application.ILoadedApplication
import eu.thesimplecloud.application.filecontent.DefaultApplicationFileContent
import eu.thesimplecloud.application.filecontent.IApplicationFileContent
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.04.2021
 * Time: 22:09
 */
class LoadedModuleApplication(
    private val file: File,
    private val fileContent: IApplicationFileContent,
    private val loadedClassInstance: AbstractModule
) : ILoadedApplication {

    override fun getFile(): File {
        return file
    }

    override fun getApplicationFileContent(): IApplicationFileContent {
        return fileContent
    }

    override fun getLoadedClassInstance(): AbstractModule {
        return loadedClassInstance
    }

}