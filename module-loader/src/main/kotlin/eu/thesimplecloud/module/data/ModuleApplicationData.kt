package eu.thesimplecloud.module.data

import eu.thesimplecloud.application.data.DefaultApplicationData

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.04.2021
 * Time: 22:09
 */
class ModuleApplicationData(
    private val defaultApplicationData: DefaultApplicationData,
    private val classToLoad: String
) : IModuleApplicationData {

    override fun getName(): String {
        return defaultApplicationData.name
    }

    override fun getAuthor(): String {
        return defaultApplicationData.author
    }

    override fun getVersion(): String {
        return defaultApplicationData.version
    }

    override fun getClassNameToLoad(): String {
        return classToLoad
    }

}