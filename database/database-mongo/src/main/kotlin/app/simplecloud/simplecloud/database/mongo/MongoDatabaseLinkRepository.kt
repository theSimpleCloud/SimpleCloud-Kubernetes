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

package app.simplecloud.simplecloud.database.mongo

import app.simplecloud.simplecloud.api.resourcedefinition.link.LinkConfiguration
import app.simplecloud.simplecloud.database.api.DatabaseLinkRepository
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Date: 29.03.23
 * Time: 18:37
 * @author Frederick Baier
 *
 */
class MongoDatabaseLinkRepository(
    private val database: MongoDatabase,
) : DatabaseLinkRepository {

    override fun loadAll(): List<LinkConfiguration> {
        val linkTypes = database.listCollectionNames()
            .filter { it.startsWith("link/") }
            .map { it.removePrefix("link/") }
            .toSet()

        return linkTypes.map { loadAll(it) }.flatten()
    }

    override fun loadAll(linkType: String): List<LinkConfiguration> {
        val collection = database.getCollection("link/$linkType")
        val documents = collection.find().toList()
        return documents.map { JsonLib.fromObject(it).getObject(LinkConfiguration::class.java) }
    }

    override fun save(configuration: LinkConfiguration) {
        val collection = database.getCollection("link/${configuration.linkType}")
        val filter = Filters.eq("oneResourceName", configuration.oneResourceName)
        val update = Updates.combine(
            Updates.set("linkType", configuration.linkType),
            Updates.set("manyResourceName", configuration.manyResourceName)
        )
        collection.updateOne(filter, update, UpdateOptions().upsert(true))
    }


    override fun delete(linkType: String, oneResourceName: String) {
        val collection = database.getCollection("link/$linkType")
        collection.deleteOne(Filters.eq("oneResourceName", oneResourceName))
    }
}
