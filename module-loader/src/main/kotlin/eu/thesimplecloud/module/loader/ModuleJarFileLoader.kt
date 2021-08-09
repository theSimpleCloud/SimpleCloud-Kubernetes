package eu.thesimplecloud.module.loader

import com.fasterxml.jackson.databind.JsonNode
import com.google.inject.Singleton
import eu.thesimplecloud.application.filecontent.DefaultApplicationFileContent
import eu.thesimplecloud.application.loader.AbstractJarFileLoader

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.04.2021
 * Time: 21:15
 */
@Singleton
class ModuleJarFileLoader : AbstractJarFileLoader<DefaultApplicationFileContent>(
    "module.json"
) {

    override fun constructCustomApplicationFileContent(
        defaultApplicationData: DefaultApplicationFileContent,
        jsonNode: JsonNode
    ): DefaultApplicationFileContent {
        return defaultApplicationData
    }


}