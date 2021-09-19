/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.api.config.mongo

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