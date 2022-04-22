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

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 12:57
 * @author Frederick Baier
 */
class Address(
    val host: String,
    val port: Int
) : java.io.Serializable {

    constructor() : this("127.0.0.1", -1)

    fun asIpString(): String {
        return "${host}:${port}"
    }

    override fun toString(): String {
        return "Address(${asIpString()})"
    }

    companion object {
        fun fromIpString(string: String): Address {
            val array = string.split(":")
            if (array.size != 2) {
                throw IllegalArgumentException(
                    "Wrong Address format. Expected format: 'host:port' (e.g. 55.55.55.55:1630) but was $string"
                )
            }
            return Address(array[0], array[1].toInt())
        }
    }

}