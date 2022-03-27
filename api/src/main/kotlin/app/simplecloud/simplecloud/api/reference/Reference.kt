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

package app.simplecloud.simplecloud.api.reference

import java.util.concurrent.CompletableFuture

/**
 * Used to reference objects.
 * So only an identifier is transmitted and the objects itself can be resolved when needed
 * @param T The type of the object
 */
interface Reference<T : Any> {

    /**
     * Resolves the object
     * @return a promise that completes when the result is ready or fails when there was an error resolving the object
     */
    fun resolveReference(): CompletableFuture<T>

}