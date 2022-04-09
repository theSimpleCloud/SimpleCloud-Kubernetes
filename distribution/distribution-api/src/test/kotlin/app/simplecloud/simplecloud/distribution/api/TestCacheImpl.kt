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

package app.simplecloud.simplecloud.distribution.api

import com.google.common.collect.Maps

class TestCacheImpl<K, V> : Cache<K, V> {

    private val map = Maps.newConcurrentMap<K, V>()

    override val size: Int
        get() = this.map.size

    override fun containsKey(key: K): Boolean {
        return this.map.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return this.map.containsValue(value)
    }

    override fun get(key: K): V? {
        return this.map.get(key)
    }

    override fun isEmpty(): Boolean {
        return this.map.isEmpty()
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = this.map.entries
    override val keys: MutableSet<K>
        get() = this.map.keys
    override val values: MutableCollection<V>
        get() = this.map.values

    override fun clear() {
        this.map.clear()
    }

    override fun put(key: K, value: V): V? {
        return this.map.put(key, value)
    }

    override fun putAll(from: Map<out K, V>) {
        this.map.putAll(from)
    }

    override fun remove(key: K): V? {
        return this.map.remove(key)
    }

}
