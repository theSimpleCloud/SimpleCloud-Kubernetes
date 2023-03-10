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

package app.simplecloud.simplecloud.module.api.resourcedefinition

/**
 * Date: 19.01.23
 * Time: 13:21
 * @author Frederick Baier
 *
 * @param O the type of the older version
 * @param N the type of the newer version
 */
interface ResourceVersionConverter<O, N> {

    /**
     * Returns the name of the older version
     */
    fun getOlderVersionName(): String

    /**
     * Returns the name of the newer version
     */
    fun getNewerVersionName(): String

    /**
     * Converts the older version to the newer version or
     * @throws ConversionNotPossibleException if the conversion is not possible
     */
    @Throws(ConversionNotPossibleException::class)
    fun convertOldToNew(oldVersion: O): N

    /**
     * Converts the newer version to the older version or
     * @throws ConversionNotPossibleException if the conversion is not possible
     */
    @Throws(ConversionNotPossibleException::class)
    fun convertNewToOld(newVersion: N): O

    class ConversionNotPossibleException : RuntimeException {
        constructor(cause: Throwable) : super(cause)
        constructor(msg: String) : super(msg)
        constructor(msg: String, cause: Throwable) : super(msg, cause)
    }

}