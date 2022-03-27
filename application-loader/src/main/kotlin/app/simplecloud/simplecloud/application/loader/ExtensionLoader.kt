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

import com.google.inject.Injector

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.03.2021
 * Time: 15:30
 */
class ExtensionLoader<C>(
    private val injector: Injector,
    private val classLoader: ClassLoader,
    private val parentClass: Class<C>
) {

    fun loadClassInstance(classpath: String): C {
        val newClass = loadClass(classpath)
        return this.injector.createChildInjector().getInstance(newClass)
    }

    private fun loadClass(classpath: String): Class<out C> {
        val clazz = Class.forName(classpath, true, this.classLoader)
        return clazz.asSubclass(this.parentClass)
    }

}