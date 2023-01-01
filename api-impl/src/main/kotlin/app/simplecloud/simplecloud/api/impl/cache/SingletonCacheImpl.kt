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

package app.simplecloud.simplecloud.api.impl.cache

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.cache.SingletonCache
import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.distribution.api.Cache
import java.util.concurrent.CompletableFuture

/**
 * Date: 31.12.22
 * Time: 11:32
 * @author Frederick Baier
 *
 */
class SingletonCacheImpl<T>(
    private val name: String,
    private val cloudAPI: CloudAPI,
) : SingletonCache<T> {

    private val cacheName: String = "single-${this.name}"

    override fun setValue(value: T) {
        val cache = this.getCache()
        cache.set("key", value)
    }

    override fun getValue(): CompletableFuture<T> {
        return CloudCompletableFuture.supplyAsync {
            val cache = this.getCache()
            return@supplyAsync cache["key"] ?: throw NoSuchElementException("Cannot find value in cache $cacheName")
        }
    }

    private fun getCache(): Cache<String, T> {
        return this.cloudAPI.getOrCreateCache(this.cacheName)
    }

}