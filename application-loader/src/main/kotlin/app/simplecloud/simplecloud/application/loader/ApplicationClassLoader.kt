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

package app.simplecloud.simplecloud.application.loader

import com.google.common.collect.Maps
import java.net.URL
import java.net.URLClassLoader

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 26.03.2021
 * Time: 21:39
 */
class ApplicationClassLoader(
    urls: List<URL>,
    parent: ClassLoader
) : URLClassLoader(urls.toTypedArray(), parent) {

    @Volatile
    private var closed: Boolean = false
    private val cachedClasses: MutableMap<String, Class<*>> = Maps.newConcurrentMap()

    companion object {
        init {
            ClassLoader.registerAsParallelCapable()
        }
    }

    override fun findClass(name: String): Class<*> {
        if (this.closed) throw IllegalStateException("ApplicationClassLoader is already closed")
        return findClass0(name)
    }

    private fun findClass0(name: String): Class<*> {
        val clazz = this.cachedClasses[name]
        if (clazz != null) return clazz
        val classByName = runCatching { super.findClass(name) }.getOrNull()
        if (classByName != null) {
            this.cachedClasses[name] = classByName
            return classByName
        }
        throw ClassNotFoundException(name)
    }

    override fun close() {
        super.close()
        this.closed = true
    }

    fun isClosed(): Boolean {
        return this.closed
    }

}