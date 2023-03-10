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

import app.simplecloud.simplecloud.api.resourcedefinition.Resource
import app.simplecloud.simplecloud.database.api.DatabaseResourceRepository
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import eu.thesimplecloud.jsonlib.JsonLib
import org.bson.Document

/**
 * Date: 15.01.23
 * Time: 13:29
 * @author Frederick Baier
 *
 */
class MongoDatabaseResourceRepository(
    private val database: MongoDatabase,
) : DatabaseResourceRepository {

    override fun save(resource: Resource) {
        if (load(resource.apiVersion, resource.kind, resource.name) != null)
            throw IllegalStateException("Resource already exists")
        val collection = database.getCollection(resource.apiVersion + "/" + resource.kind)
        collection.insertOne(JsonLib.fromObject(resource).getObject(Document::class.java))
    }

    override fun update(resource: Resource) {
        if (load(resource.apiVersion, resource.kind, resource.name) == null)
            throw IllegalStateException("Resource does not exist")
        val collection = database.getCollection(resource.apiVersion + "/" + resource.kind)
        val updateObj = BasicDBObject()
        updateObj["\$set"] = JsonLib.fromObject(resource).getObject(Document::class.java)
        collection.updateOne(Filters.eq("name", resource.name), updateObj)
    }

    override fun load(apiVersion: String, kind: String, name: String): Resource? {
        val collection = database.getCollection("$apiVersion/$kind")
        val document = collection.find(Filters.eq("name", name)).first() ?: return null
        return JsonLib.fromObject(document).getObject(Resource::class.java)
    }

    override fun loadAll(apiVersion: String, kind: String): List<Resource> {
        val collection = database.getCollection("$apiVersion/$kind")
        val documents = collection.find().toList()
        return documents.map { JsonLib.fromObject(it).getObject(Resource::class.java) }
    }

    override fun delete(apiVersion: String, kind: String, name: String) {
        val collection = database.getCollection("$apiVersion/$kind")
        collection.deleteOne(Filters.eq("name", name))
    }
}