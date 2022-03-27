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

package app.simplecloud.simplecloud.api.future

import app.simplecloud.simplecloud.api.future.exception.CompletedWithNullException
import app.simplecloud.simplecloud.api.future.exception.FutureOriginException
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutionException

/**
 * Created by IntelliJ IDEA.
 * Date: 29.03.2021
 * Time: 23:50
 * @author Frederick Baier
 */

/**
 * Creates a not nullable future
 */
fun <T> CompletableFuture<T?>.nonNull(exception: Exception = CompletedWithNullException()): CompletableFuture<T> {
    return this.thenApply { it ?: throw exception }
}

/**
 * Creates a nullable future
 */
fun <T> CompletableFuture<T>.nullable(): CompletableFuture<T?> {
    val returnFuture = CloudCompletableFuture<T?>()
    this.handle { value, throwable ->
        if (this.isCompletedExceptionally) {
            if (throwable is CompletedWithNullException)
                returnFuture.complete(null)
            returnFuture.completeExceptionally(throwable)
        }
        returnFuture.complete(value)
    }
    return returnFuture
}

fun <T> List<CompletableFuture<out T>>.toFutureList(): CompletableFuture<List<T>> {
    if (isEmpty()) return completedFuture(emptyList())
    val returnFuture = CloudCompletableFuture<List<T>>()
    val list: MutableList<T> = CopyOnWriteArrayList<T>()
    this.map { future ->
        future.handle { _, _ ->
            handleFutureComplete(future, list)
            if (this.all { it.isDone }) {
                returnFuture.complete(list)
            }
        }
    }

    return returnFuture
}

fun <T> handleFutureComplete(future: CompletableFuture<T>, resultList: MutableList<in T>) {
    if (!future.isCompletedExceptionally) {
        resultList.add(future.get())
    }
}

fun <T> CompletableFuture<CompletableFuture<T>>.flatten(): CompletableFuture<T> {
    val returnFuture = CloudCompletableFuture<T>()
    this.handle { result, throwable ->
        if (this.isCompletedExceptionally) {
            returnFuture.completeExceptionally(throwable)
        } else {
            result.copyTo(returnFuture)
        }
    }
    return returnFuture
}

fun <T> CompletableFuture<T>.copyTo(otherFuture: CompletableFuture<T>) {
    this.handle { result, throwable ->
        if (otherFuture.isDone) return@handle

        if (this.isCompletedExceptionally) {
            otherFuture.completeExceptionally(throwable)
        } else {
            otherFuture.complete(result)
        }
    }
}

fun List<CompletableFuture<*>>.combineToUnitFuture(): CompletableFuture<Unit> {
    val returnFuture = CloudCompletableFuture<Unit>()

    for (future in this) {
        future.handle { _, _ ->
            if (this.all { it.isDone }) {
                returnFuture.complete(null)
            }
        }
    }

    return returnFuture
}

fun <T> CompletableFuture<T>.getNow(): T {
    if (!this.isDone || this.isCompletedExceptionally) {
        throw NullPointerException("Future is not completed yet or completed exceptionally")
    }
    return this.getNow(null)!!
}

fun <T> CompletableFuture<T>.getNowOrNull(): T? {
    return this.getNow(null)
}

val CompletableFuture<*>.isCompletedNormally: Boolean
    get() = this.isDone && !this.isCompletedExceptionally

fun CompletableFuture<*>.toUnitFuture(): CompletableFuture<Unit> {
    val resultFuture = CloudCompletableFuture<Unit>()
    this.handle { _, throwable ->
        if (isCompletedExceptionally) {
            resultFuture.completeExceptionally(throwable)
        } else {
            resultFuture.complete(Unit)
        }
    }
    return resultFuture
}

fun unwrapFutureException(ex: Throwable): Throwable {
    if (ex is CompletionException) {
        return unwrapFutureException(ex.cause!!)
    }
    if (ex is InvocationTargetException) {
        return unwrapFutureException(ex.cause!!)
    }
    if (ex is FutureOriginException) {
        return unwrapFutureException(ex.cause!!)
    }
    if (ex is ExecutionException) {
        return unwrapFutureException(ex.cause!!)
    }

    return ex
}