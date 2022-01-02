package app.simplecloud.simplecloud.module.loader

import app.simplecloud.simplecloud.application.filecontent.DefaultApplicationFileContent
import app.simplecloud.simplecloud.application.loader.AbstractJarFileLoader
import com.fasterxml.jackson.databind.JsonNode
import com.google.inject.Singleton

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