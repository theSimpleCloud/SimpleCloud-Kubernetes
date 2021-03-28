package eu.thesimplecloud.application

import eu.thesimplecloud.application.data.IApplicationData
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 28.03.2021
 * Time: 18:22
 */
interface ILoadedApplication <D: IApplicationData, C>{

    fun getFile(): File

    fun getApplicationData(): D

    fun getLoadedClassInstance(): C

}