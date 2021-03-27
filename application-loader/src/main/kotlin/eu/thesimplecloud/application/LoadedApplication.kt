package eu.thesimplecloud.application

import eu.thesimplecloud.application.data.IApplicationData
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.03.2021
 * Time: 21:15
 */
class LoadedApplication<T>(val file: File, val applicationData: IApplicationData, val loadedClassInstance: T)