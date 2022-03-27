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

import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 17.03.2021
 * Time: 17:12
 * @author Frederick Baier
 *
 * Represents a request
 * The request is filled with information and then submitted
 *
 */
interface Request<T : Any> {

    /**
     * Submits the request
     * @return a promise completing with the result of the request
     */
    fun submit(): CompletableFuture<T>

}