/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
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