package eu.thesimplecloud.application

import eu.thesimplecloud.application.filecontent.ApplicationFileContent
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 28.03.2021
 * Time: 18:22
 */
interface LoadedApplication {

    fun getFile(): File

    fun getApplicationFileContent(): ApplicationFileContent

    fun getLoadedClassInstance(): Any

}