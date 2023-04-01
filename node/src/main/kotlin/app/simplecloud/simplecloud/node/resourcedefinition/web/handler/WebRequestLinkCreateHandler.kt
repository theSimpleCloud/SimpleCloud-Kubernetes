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

package app.simplecloud.simplecloud.node.resourcedefinition.web.handler

import app.simplecloud.simplecloud.api.resourcedefinition.link.LinkConfiguration
import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.schema.SchemaCreator
import app.simplecloud.simplecloud.module.api.internal.service.InternalLinkService
import app.simplecloud.simplecloud.node.resourcedefinition.SchemaValidator
import eu.thesimplecloud.jsonlib.JsonLib
import kotlinx.coroutines.runBlocking
import org.yaml.snakeyaml.Yaml

/**
 * Date: 29.03.23
 * Time: 21:39
 * @author Frederick Baier
 *
 */
class WebRequestLinkCreateHandler(
    private val body: String,
    private val linkService: InternalLinkService,
) {

    private val schema = SchemaCreator(LinkConfiguration::class.java, false, emptyList()).createSchema()

    fun handleCreate() {
        val jsonLib = convertBodyToJson()
        SchemaValidator(schema, jsonLib).validate()
        val linkConfiguration = jsonLib.getObject(LinkConfiguration::class.java)
        runBlocking {
            linkService.createLinkInternal(linkConfiguration)
        }
    }

    private fun convertBodyToJson(): JsonLib {
        val map = Yaml().loadAs(this.body, Map::class.java)
        return JsonLib.fromObject(map)
    }

}