package app.simplecloud.simplecloud.module

import app.simplecloud.simplecloud.application.LoadedApplication
import app.simplecloud.simplecloud.application.filecontent.ApplicationFileContent
import com.google.inject.AbstractModule
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.04.2021
 * Time: 22:09
 */
class LoadedModuleApplication(
    private val file: File,
    private val fileContent: ApplicationFileContent,
    private val loadedClassInstance: AbstractModule
) : LoadedApplication {

    override fun getFile(): File {
        return file
    }

    override fun getApplicationFileContent(): ApplicationFileContent {
        return fileContent
    }

    override fun getLoadedClassInstance(): AbstractModule {
        return loadedClassInstance
    }

}