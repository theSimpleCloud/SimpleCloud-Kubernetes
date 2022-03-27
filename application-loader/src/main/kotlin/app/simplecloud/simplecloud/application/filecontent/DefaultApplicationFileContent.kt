/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.application.filecontent

import app.simplecloud.simplecloud.application.exception.InvalidApplicationEntryPointFileException
import com.fasterxml.jackson.databind.JsonNode

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