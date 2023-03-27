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

import app.simplecloud.simplecloud.api.resourcedefinition.Resource
import app.simplecloud.simplecloud.database.api.DatabaseResourceRepository
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Date: 15.01.23
 * Time: 13:13
 * @author Frederick Baier
 *
 */
class MemoryDatabaseResourceRepository : DatabaseResourceRepository {

    private val resources = CopyOnWriteArrayList<Resource>()
    override fun save(resource: Resource) {
        this.resources.add(resource)
    }

    override fun update(resource: Resource) {
        this.resources.removeIf { it.kind == resource.kind && it.apiVersion == resource.apiVersion && it.name == resource.name }
        this.resources.add(resource)
    }

    override fun load(apiVersion: String, kind: String, fieldName: String, fieldValue: String): Resource? {
        if (fieldName == "name") {
            return this.resources.firstOrNull { it.kind == kind && it.apiVersion == apiVersion && it.name == fieldValue }
        }
        if (fieldName.startsWith("spec.")) {
            val propertyName = fieldName.replaceFirst("spec.", "")
            return this.resources.firstOrNull { it.kind == kind && it.apiVersion == apiVersion && it.spec[propertyName].toString() == fieldValue }
        }
        throw IllegalArgumentException("Invalid FieldName: $fieldName")
    }

    override fun loadAll(apiVersion: String, kind: String): List<Resource> {
        return this.resources.filter { it.apiVersion == apiVersion && it.kind == kind }
    }

    override fun delete(apiVersion: String, kind: String, name: String) {
        this.resources.removeIf { it.kind == kind && it.apiVersion == apiVersion && it.name == name }
    }
}