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

package app.simplecloud.simplecloud.api.repository

import app.simplecloud.simplecloud.api.future.isCompletedNormally
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 22.03.2021
 * Time: 18:32
 * @author Frederick Baier
 *
 *  @param I identifier
 *  @param T type to be stored
 */
interface Repository<I : Any, T : Any> {

    /**
     * Returns all values stored
     */
    fun findAll(): CompletableFuture<List<T>>

    /**
     * Returns then first element found
     */
    fun findFirst(): CompletableFuture<T>

    /**
     * Returns the object found by the specified [identifier]
     */
    fun find(identifier: I): CompletableFuture<T>

    /**
     * Returns the object found by the specified [identifier] or null
     */
    fun findOrNull(identifier: I): CompletableFuture<T?>

    /**
     * Saves the specified [value] and replaces it if needed according to its identifier
     */
    fun save(identifier: I, value: T): CompletableFuture<Unit>

    /**
     * Removes the value found by the specified [identifier]
     */
    fun remove(identifier: I): CompletableFuture<Unit>

    /**
     * Checks whether the specified [identifier] exists
     */
    fun doesExist(identifier: I): CompletableFuture<Boolean> {
        val future = find(identifier)
        return future.handle { _, _ -> future.isCompletedNormally }
    }

    /**
     * Returns the count of elements stored in this repository
     */
    fun count(): CompletableFuture<Long>

}