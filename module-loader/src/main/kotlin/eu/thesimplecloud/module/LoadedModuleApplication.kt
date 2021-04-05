package eu.thesimplecloud.module

import com.google.inject.AbstractModule
import eu.thesimplecloud.application.ILoadedApplication
import eu.thesimplecloud.module.data.IModuleApplicationData
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.04.2021
 * Time: 22:09
 */
class LoadedModuleApplication(
    private val file: File,
    private val applicationData: IModuleApplicationData,
    private val loadedClassInstance: AbstractModule
) : ILoadedApplication<IModuleApplicationData, AbstractModule> {

    override fun getFile(): File {
        return file
    }

    override fun getApplicationData(): IModuleApplicationData {
        return applicationData
    }

    override fun getLoadedClassInstance(): AbstractModule {
        return loadedClassInstance
    }

}