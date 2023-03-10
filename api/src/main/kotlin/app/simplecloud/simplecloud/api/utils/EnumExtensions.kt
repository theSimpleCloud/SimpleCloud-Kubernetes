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

package app.simplecloud.simplecloud.api.utils

/**
 * Date: 19.01.23
 * Time: 18:34
 * @author Frederick Baier
 *
 */
fun Class<out Enum<*>>.getEnumValues(): List<String> {
    val method = this.getMethod("values")
    val values = method.invoke(null) as Array<Enum<*>>
    return values.map { it.name }
}

fun <T : Enum<*>> Class<out T>.enumValueOf(string: String): T {
    val method = this.getMethod("valueOf", String::class.java)
    return method.invoke(null, string) as T
}