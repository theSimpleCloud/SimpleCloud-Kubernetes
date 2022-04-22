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

package app.simplecloud.simplecloud.distibution.hazelcast

import app.simplecloud.simplecloud.distribution.api.Cache
import app.simplecloud.simplecloud.distribution.api.EntryListener
import app.simplecloud.simplecloud.distribution.api.Predicate
import com.google.common.collect.Maps
import com.hazelcast.core.EntryEvent
import com.hazelcast.map.IMap
import java.util.*


class HazelCastCache<K, V>(
    private val hazelMap: IMap<K, V>
) : Cache<K, V> {

    private val listenerToId = Maps.newConcurrentMap<EntryListener<K, V>, UUID>()

    override fun getName(): String {
        return this.hazelMap.name
    }

    override fun first(): Map.Entry<K, V> {
        return this.hazelMap.first()
    }

    override val size: Int
        get() = this.hazelMap.size

    override fun containsKey(key: K): Boolean {
        return this.hazelMap.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return this.hazelMap.containsValue(value)
    }

    override fun get(key: K): V? {
        return this.hazelMap.get(key)
    }

    override fun isEmpty(): Boolean {
        return this.hazelMap.isEmpty
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = this.hazelMap.entries
    override val keys: MutableSet<K>
        get() = this.hazelMap.keys
    override val values: MutableCollection<V>
        get() = this.hazelMap.values

    override fun clear() {
        this.hazelMap.clear()
    }

    override fun put(key: K, value: V): V? {
        return this.hazelMap.put(key, value)
    }

    override fun putAll(from: Map<out K, V>) {
        this.hazelMap.putAll(from)
    }

    override fun remove(key: K): V? {
        return this.hazelMap.remove(key)
    }

    override fun addEntryListener(entryListener: EntryListener<K, V>) {
        val mapListener = object : HazelCastEntryListener<K, V> {
            override fun entryAdded(event: EntryEvent<K, V>) {
                entryListener.entryUpdated(event.key to event.value)
            }

            override fun entryUpdated(event: EntryEvent<K, V>) {
                entryListener.entryUpdated(event.key to event.value)
            }

            override fun entryRemoved(event: EntryEvent<K, V>) {
                entryListener.entryRemoved(event.key to event.oldValue)
            }

        }
        val listenerId = this.hazelMap.addEntryListener(mapListener, true)
        this.listenerToId[entryListener] = listenerId
    }

    override fun distributedQuery(predicate: Predicate<K, V>): Collection<V> {
        return this.hazelMap.values { predicate.apply(it.key, it.value) }
    }

}
