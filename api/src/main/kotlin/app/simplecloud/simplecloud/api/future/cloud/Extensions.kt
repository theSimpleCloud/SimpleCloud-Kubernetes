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

package app.simplecloud.simplecloud.api.future.cloud

import app.simplecloud.simplecloud.api.future.copyTo
import app.simplecloud.simplecloud.api.future.exception.CompletedWithNullException
import app.simplecloud.simplecloud.api.utils.future.CloudCompletableFuture
import java.util.concurrent.CompletableFuture


//CloudCompletableFuture


/**
 * Creates a not nullable future
 */
fun <T> CloudCompletableFuture<T?>.nonNull(): CloudCompletableFuture<T> {
    return this.thenApply { it ?: throw CompletedWithNullException() }
}

/**
 * Creates a nullable future
 */
fun <T> CloudCompletableFuture<T>.nullable(): CloudCompletableFuture<T?> {
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

fun <T> CloudCompletableFuture<CompletableFuture<T>>.flatten(): CloudCompletableFuture<T> {
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

fun <T> CloudCompletableFuture<T>.copyTo(otherFuture: CompletableFuture<T>) {
    this.handle { result, throwable ->
        if (otherFuture.isDone) return@handle

        if (this.isCompletedExceptionally) {
            otherFuture.completeExceptionally(throwable)
        } else {
            otherFuture.complete(result)
        }
    }
}

fun List<CloudCompletableFuture<*>>.combineToVoidFuture(): CloudCompletableFuture<Void> {
    val returnFuture = CloudCompletableFuture<Void>()

    for (future in this) {
        future.handle { _, _ ->
            if (this.all { it.isDone }) {
                returnFuture.complete(null)
            }
        }
    }

    return returnFuture
}

fun <T> CloudCompletableFuture<T>.getNow(): T {
    if (!this.isDone || this.isCompletedExceptionally) {
        throw NullPointerException("Future is not completed yet or completed exceptionally")
    }
    return this.getNow(null)!!
}

fun <T> CloudCompletableFuture<T>.getNowOrNull(): T? {
    return this.getNow(null)
}

val CloudCompletableFuture<*>.isCompletedNormally: Boolean
    get() = this.isDone && !this.isCompletedExceptionally

fun CloudCompletableFuture<*>.toUnitFuture(): CloudCompletableFuture<Unit> {
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