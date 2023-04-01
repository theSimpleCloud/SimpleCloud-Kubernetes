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

package app.simplecloud.simplecloud.database.memory

import app.simplecloud.simplecloud.api.resourcedefinition.link.LinkConfiguration
import app.simplecloud.simplecloud.database.api.DatabaseLinkRepository

/**
 * Date: 29.03.23
 * Time: 19:16
 * @author Frederick Baier
 *
 */
class MemoryDatabaseLinkRepository : DatabaseLinkRepository {

    private val linksByType: MutableMap<String, MutableList<LinkConfiguration>> = mutableMapOf()

    override fun loadAll(): List<LinkConfiguration> {
        return linksByType.flatMap { it.value }
    }

    override fun loadAll(linkType: String): List<LinkConfiguration> {
        return linksByType[linkType]?.toList() ?: emptyList()
    }

    override fun save(configuration: LinkConfiguration) {
        val linkType = configuration.linkType
        val existingLinks = linksByType.getOrDefault(linkType, mutableListOf())
        existingLinks.removeIf { it.oneResourceName == configuration.oneResourceName }
        existingLinks.add(configuration)
        linksByType[linkType] = existingLinks
    }

    override fun delete(linkType: String, oneResourceName: String) {
        val existingLinks = linksByType.getOrDefault(linkType, mutableListOf())
        existingLinks.removeIf { it.oneResourceName == oneResourceName }
        linksByType[linkType] = existingLinks
    }
}
