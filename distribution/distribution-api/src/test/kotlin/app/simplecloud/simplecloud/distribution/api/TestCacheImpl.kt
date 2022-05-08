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
import java.util.concurrent.CopyOnWriteArrayList

class TestCacheImpl<K, V>(
    private val name: String
) : Cache<K, V> {

    private val map = Maps.newConcurrentMap<K, V>()

    private val entryListeners = CopyOnWriteArrayList<EntryListener<K, V>>()

    override fun getName(): String {
        return this.name
    }

    override fun first(): Map.Entry<K, V> {
        return this.map.entries.first()
    }

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
        val result = this.map.put(key, value)
        if (result == null) {
            this.entryListeners.forEach { it.entryAdded(key to value) }
        } else {
            this.entryListeners.forEach { it.entryUpdated(key to value) }
        }
        return result
    }

    override fun putAll(from: Map<out K, V>) {
        this.map.putAll(from)
    }

    override fun remove(key: K): V? {
        val result = this.map.remove(key)
        if (result != null)
            this.entryListeners.forEach { it.entryRemoved(key to result) }
        return result
    }

    override fun addEntryListener(entryListener: EntryListener<K, V>) {
        this.entryListeners.add(entryListener)
    }

    override fun distributedQuery(predicate: Predicate<K, V>): Collection<V> {
        return this.entries.filter { predicate.apply(it.key, it.value) }.map { it.value }
    }

}
