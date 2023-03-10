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

package app.simplecloud.simplecloud.module.api.resourcedefinition.request

import app.simplecloud.simplecloud.module.api.resourcedefinition.request.exception.NoSuchResourceDefinitionException
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.exception.ResourceAlreadyExistsException

/**
 * Date: 08.03.23
 * Time: 10:45
 * @author Frederick Baier
 *
 */
interface ResourceRequestHandler {

    /**
     * Returns all resources found
     * @throws NoSuchResourceDefinitionException if the ResourceDefinition cannot be found
     */
    fun handleGetAll(group: String, kind: String, version: String): List<RequestResult>

    /**
     * Returns all resources found
     * @throws NoSuchResourceDefinitionException if the ResourceDefinition cannot be found
     */
    fun <S : Any> handleGetAllSpec(group: String, kind: String, version: String): List<RequestSpecResult<S>>

    /**
     * Returns all resources found
     * @throws NoSuchResourceDefinitionException if the ResourceDefinition cannot be found
     */
    fun <SPEC : Any, STATUS : Any> handleGetAllSpecAndStatus(
        group: String,
        kind: String,
        version: String,
    ): List<RequestSpecAndStatusResult<SPEC, STATUS>>

    /**
     * Returns the resource or
     * @throws NoSuchResourceDefinitionException if the ResourceDefinition cannot be found
     * @throws NoSuchElementException if the resource cannot be found
     */
    fun handleGetOne(group: String, kind: String, version: String, name: String): RequestResult

    /**
     * Returns the resource or
     * @throws NoSuchResourceDefinitionException if the ResourceDefinition cannot be found
     * @throws NoSuchElementException if the resource cannot be found
     */
    fun <S : Any> handleGetOneSpec(group: String, kind: String, version: String, name: String): RequestSpecResult<S>

    /**
     * Returns the resource or
     * @throws NoSuchResourceDefinitionException if the ResourceDefinition cannot be found
     * @throws NoSuchElementException if the resource cannot be found
     */
    fun <SPEC : Any, STATUS : Any> handleGetOneSpecAndStatus(
        group: String,
        kind: String,
        version: String,
        name: String,
    ): RequestSpecAndStatusResult<SPEC, STATUS>

    /**
     * Creates the resource and saves it
     * @throws NoSuchResourceDefinitionException if the ResourceDefinition cannot be found
     * @throws ResourceAlreadyExistsException if the Resource that shall be created does already exist
     */
    fun handleCreate(group: String, kind: String, version: String, name: String, spec: Any)

    /**
     * Updates the resource and saves it
     * @throws NoSuchElementException if the resource cannot be found
     */
    fun handleUpdate(group: String, kind: String, version: String, name: String, spec: Any)

    /**
     * Deletes the resource
     * @throws NoSuchElementException if the resource cannot be found
     */
    fun handleDelete(group: String, kind: String, version: String, name: String)

}