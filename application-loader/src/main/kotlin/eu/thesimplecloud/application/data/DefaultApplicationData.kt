package eu.thesimplecloud.application.data

import com.fasterxml.jackson.databind.JsonNode
import eu.thesimplecloud.application.exception.InvalidApplicationEntryPointFileException

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.03.2021
 * Time: 13:42
 */
data class DefaultApplicationData(
    val name: String,
    val author: String,
    val version: String = ""
) {

    companion object {

        fun fromJsonNode(jsonNode: JsonNode): DefaultApplicationData {
            val applicationName = jsonNode.path("name").textValue()?:  throw InvalidApplicationEntryPointFileException("name")
            val applicationAuthor = jsonNode.path("author").textValue()?:  throw InvalidApplicationEntryPointFileException("author")
            val applicationVersion = jsonNode.path("version").textValue()?: "1.0"

            return DefaultApplicationData(applicationName, applicationAuthor, applicationVersion)
        }

    }

}