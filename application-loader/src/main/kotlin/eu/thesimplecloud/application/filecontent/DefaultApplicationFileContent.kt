package eu.thesimplecloud.application.filecontent

import com.fasterxml.jackson.databind.JsonNode
import eu.thesimplecloud.application.exception.InvalidApplicationEntryPointFileException

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.03.2021
 * Time: 13:42
 */
open class DefaultApplicationFileContent(
    private val name: String,
    private val author: String,
    private val version: String = "",
    private val classToLoad: String
) : ApplicationFileContent {

    override fun getName(): String {
        return this.name
    }

    override fun getAuthor(): String {
        return this.author
    }

    override fun getVersion(): String {
        return this.version
    }

    override fun getClassNameToLoad(): String {
        return this.classToLoad
    }

    companion object {

        fun fromJsonNode(jsonNode: JsonNode): DefaultApplicationFileContent {
            val applicationName = jsonNode.path("name").textValue()
                ?: throw InvalidApplicationEntryPointFileException("name")
            val applicationAuthor = jsonNode.path("author").textValue()
                ?: throw InvalidApplicationEntryPointFileException("author")
            val classToLoad = jsonNode.path("classToLoad").textValue()
                ?: throw InvalidApplicationEntryPointFileException("classToLoad")
            val applicationVersion = jsonNode.path("version").textValue() ?: "1.0"

            return DefaultApplicationFileContent(applicationName, applicationAuthor, applicationVersion, classToLoad)
        }

    }

}