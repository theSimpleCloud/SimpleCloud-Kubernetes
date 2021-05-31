package eu.thesimplecloud.api.impl.future

import eu.thesimplecloud.api.impl.future.exception.CompletedWithNullException
import java.lang.NullPointerException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 29.03.2021
 * Time: 23:50
 * @author Frederick Baier
 */

fun <T> CompletableFuture<T?>.nonNull(): CompletableFuture<T> {
    return this.thenApply { it ?: throw CompletedWithNullException() }
}

fun <T> List<CompletableFuture<out T>>.toFutureList(): CompletableFuture<List<T>> {
    val returnFuture = CompletableFuture<List<T>>()
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

private fun <T> handleFutureComplete(future: CompletableFuture<T>, resultList: MutableList<in T>) {
    if (!future.isCompletedExceptionally) {
        resultList.add(future.get())
    }
}

fun <T> CompletableFuture<CompletableFuture<T>>.flatten(): CompletableFuture<T> {
    val returnFuture = CompletableFuture<T>()
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

fun List<CompletableFuture<*>>.combineToVoidFuture(): CompletableFuture<Void> {
    val returnFuture = CompletableFuture<Void>()

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
