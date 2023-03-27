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
 * Date: 04.02.23
 * Time: 15:28
 * @author Frederick Baier
 *
 * @param S spec type
 * The methods below can be overwritten and used to change the behaviour for requests
 */
open class ResourceVersionRequestPrePostProcessor<S> {

    open fun preGetOne(
        group: String,
        version: String,
        kind: String,
        fieldName: String,
        fieldValue: String,
    ): RequestPreProcessorResult<Any> {
        return RequestPreProcessorResult.continueNormally()
    }

    open fun preGetAll(group: String, version: String, kind: String): RequestPreProcessorResult<Any> {
        return RequestPreProcessorResult.continueNormally()
    }

    open fun preUpdate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: S,
    ): RequestPreProcessorResult<S> {
        return RequestPreProcessorResult.continueNormally()
    }

    open fun preCreate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: S,
    ): RequestPreProcessorResult<S> {
        return RequestPreProcessorResult.continueNormally()
    }

    open fun preDelete(group: String, version: String, kind: String, name: String): RequestPreProcessorResult<Any> {
        return RequestPreProcessorResult.continueNormally()
    }

    open fun postCreate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: S,
    ) {
    }

    open fun postUpdate(
        group: String,
        version: String,
        kind: String,
        name: String,
        spec: S,
    ) {
    }

    open fun postDelete(group: String, version: String, kind: String, name: String, deletedSpec: S) {}

    fun checkConstraint(value: Boolean, message: String) {
        if (!value) {
            throw ConstraintViolationException(message)
        }
    }

    sealed interface RequestPreProcessorResult<T> {

        companion object {
            @JvmStatic
            fun <T> continueNormally(): RequestPreProcessorResult<T> {
                return ContinueResult()
            }

            @JvmStatic
            fun <T> unsupportedRequest(): RequestPreProcessorResult<T> {
                return UnsupportedRequest()
            }

            @JvmStatic
            fun <T> overwriteSpec(spec: T): RequestPreProcessorResult<T> {
                return OverwriteSpec(spec)
            }

            /**
             * Blocks the database action silently
             */
            @JvmStatic
            fun <T> blockSilently(): RequestPreProcessorResult<T> {
                return BlockResult()
            }
        }

    }

    class ContinueResult<T>() : RequestPreProcessorResult<T>
    class OverwriteSpec<T>(val spec: T) : RequestPreProcessorResult<T>
    class BlockResult<T>() : RequestPreProcessorResult<T>
    class UnsupportedRequest<T>() : RequestPreProcessorResult<T>

    class ConstraintViolationException(msg: String) : Exception(msg)

}