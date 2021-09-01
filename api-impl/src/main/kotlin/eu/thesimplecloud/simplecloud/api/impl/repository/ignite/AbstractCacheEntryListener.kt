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

package eu.thesimplecloud.simplecloud.api.impl.repository.ignite

import org.apache.ignite.IgniteCache
import javax.cache.configuration.MutableCacheEntryListenerConfiguration
import javax.cache.event.*

abstract class AbstractCacheEntryListener<K, V>(
    oldValueRequired: Boolean,
    synchronous: Boolean,
) : CacheEntryCreatedListener<K, V>, CacheEntryExpiredListener<K, V>, CacheEntryRemovedListener<K, V>,
    CacheEntryUpdatedListener<K, V>, CacheEntryEventFilter<K, V> {
    private val cacheEntryConfiguration = MutableCacheEntryListenerConfiguration(
        { this@AbstractCacheEntryListener }, { this@AbstractCacheEntryListener }, oldValueRequired, synchronous
    )

    open fun filterEntry(event: CacheEntryEvent<out K, out V>): Boolean = true

    override fun onCreated(events: MutableIterable<CacheEntryEvent<out K, out V>>) {}

    override fun onExpired(events: MutableIterable<CacheEntryEvent<out K, out V>>) {}

    override fun onRemoved(events: MutableIterable<CacheEntryEvent<out K, out V>>) {}

    override fun onUpdated(events: MutableIterable<CacheEntryEvent<out K, out V>>) {}

    final override fun evaluate(event: CacheEntryEvent<out K, out V>): Boolean = filterEntry(event)

    fun register(cache: IgniteCache<K, V>) = cache.registerCacheEntryListener(cacheEntryConfiguration)

    fun unregister(cache: IgniteCache<K, V>) = cache.deregisterCacheEntryListener(cacheEntryConfiguration)
}