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

import app.simplecloud.simplecloud.module.api.service.LinkService

/**
 * Date: 30.03.23
 * Time: 17:37
 * @author Frederick Baier
 *
 */
class WebRequestLinkDefinitionsGetHandler(
    private val linkService: LinkService,
) {

    fun handleGet(): Any {
        return this.linkService.findAllLinkDefinitions().map {
            LinkDefinitionDto(
                it.getName(),
                it.getOneResourceGroup() + "/" + it.getOneResourceKind(),
                it.getManyResourceGroup() + "/" + it.getManyResourceKind()
            )
        }
    }

    class LinkDefinitionDto(
        val name: String,
        val oneResourceName: String,
        val manyResourceName: String,
    )

}