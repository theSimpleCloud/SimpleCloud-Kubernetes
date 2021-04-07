package eu.thesimplecloud.module.loader

import com.fasterxml.jackson.databind.JsonNode
import com.google.inject.Singleton
import eu.thesimplecloud.application.data.DefaultApplicationData
import eu.thesimplecloud.application.exception.InvalidApplicationEntryPointFileException
import eu.thesimplecloud.application.loader.AbstractJarFileLoader
import eu.thesimplecloud.module.data.IModuleApplicationData
import eu.thesimplecloud.module.data.ModuleApplicationData
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.04.2021
 * Time: 21:15
 */
@Singleton
class ModuleJarFileLoader: AbstractJarFileLoader<IModuleApplicationData>() {

    override fun constructApplicationData(
        defaultApplicationData: DefaultApplicationData,
        jsonNode: JsonNode
    ): IModuleApplicationData {
        val classNameToLoad = jsonNode.path("abstractModule").textValue()
            ?: throw InvalidApplicationEntryPointFileException("abstractModule")
        return ModuleApplicationData(defaultApplicationData, classNameToLoad)
    }

    override fun loadJsonFileInJar(file: File): IModuleApplicationData {
        return loadJsonFileInJar(file, "module.json")
    }


}