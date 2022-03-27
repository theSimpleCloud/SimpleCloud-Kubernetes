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

package app.simplecloud.simplecloud.api.config.mongo

import com.google.inject.Provider
import dev.morphia.Datastore
import java.util.function.Supplier

class MongoConfigProvider<T>(
    private val datastore: Datastore,
    private val entityClass: Class<T>,
    private val defaultObjectSupplier: Supplier<T>
) : Provider<T> {

    override fun get(): T {
        return this.datastore.find(this.entityClass).first() ?: return saveDefaultObject()
    }

    private fun saveDefaultObject(): T {
        val defaultObject = this.defaultObjectSupplier.get()
        saveToDatabase(defaultObject)
        return defaultObject
    }

    fun saveToDatabase(value: T) {
        this.datastore.find(this.entityClass).delete()
        this.datastore.save(value)
    }

    fun doesConfigExist(): Boolean {
        return this.datastore.find(this.entityClass).count() != 0L
    }

}