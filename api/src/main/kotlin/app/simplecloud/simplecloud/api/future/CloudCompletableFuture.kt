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

import app.simplecloud.simplecloud.api.future.exception.FutureOriginException
import java.util.concurrent.*
import java.util.function.*
import java.util.function.Function

/**
 * Created by IntelliJ IDEA.
 * Date: 27/08/2021
 * Time: 10:40
 * @author Frederick Baier
 */
class CloudCompletableFuture<T> : CompletableFuture<T>() {

    private val originException = FutureOriginException()

    override fun <U : Any?> newIncompleteFuture(): CloudCompletableFuture<U> {
        return CloudCompletableFuture()
    }

    private fun <T> appendExceptionally(future: CompletableFuture<T>): CloudCompletableFuture<T> {
        future as CloudCompletableFuture<T>
        return future.exceptionally {
            originException.initCause(it)
            throw originException
        }
    }

    override fun exceptionally(fn: Function<Throwable, out T>): CloudCompletableFuture<T> {
        return super.exceptionally(fn) as CloudCompletableFuture<T>
    }

    override fun <U : Any?> thenApply(fn: Function<in T, out U>): CloudCompletableFuture<U> {
        val future = super.thenApply(fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?> thenApplyAsync(fn: Function<in T, out U>): CloudCompletableFuture<U> {
        val future = super.thenApplyAsync(fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?> thenApplyAsync(fn: Function<in T, out U>, executor: Executor): CloudCompletableFuture<U> {
        val future = super.thenApplyAsync(fn, executor)
        return appendExceptionally(future)
    }

    override fun thenAccept(action: Consumer<in T>): CloudCompletableFuture<Void> {
        val future = super.thenAccept(action)
        return appendExceptionally(future)
    }

    override fun thenAcceptAsync(action: Consumer<in T>): CloudCompletableFuture<Void> {
        val future = super.thenAcceptAsync(action)
        return appendExceptionally(future)
    }

    override fun thenAcceptAsync(action: Consumer<in T>, executor: Executor): CloudCompletableFuture<Void> {
        val future = super.thenAcceptAsync(action, executor)
        return appendExceptionally(future)
    }

    override fun thenRun(action: Runnable): CloudCompletableFuture<Void> {
        val future = super.thenRun(action)
        return appendExceptionally(future)
    }

    override fun thenRunAsync(action: Runnable): CloudCompletableFuture<Void> {
        val future = super.thenRunAsync(action)
        return appendExceptionally(future)
    }

    override fun thenRunAsync(action: Runnable, executor: Executor): CloudCompletableFuture<Void> {
        val future = super.thenRunAsync(action, executor)
        return appendExceptionally(future)
    }

    override fun <U : Any?, V : Any?> thenCombine(
        other: CompletionStage<out U>?,
        fn: BiFunction<in T, in U, out V>
    ): CloudCompletableFuture<V> {
        val future = super.thenCombine(other, fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?, V : Any?> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>
    ): CloudCompletableFuture<V> {
        val future = super.thenCombineAsync(other, fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?, V : Any?> thenCombineAsync(
        other: CompletionStage<out U>,
        fn: BiFunction<in T, in U, out V>,
        executor: Executor
    ): CloudCompletableFuture<V> {
        val future = super.thenCombineAsync(other, fn, executor)
        return appendExceptionally(future)
    }

    override fun <U : Any?> thenAcceptBoth(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>
    ): CloudCompletableFuture<Void> {
        val future = super.thenAcceptBoth(other, action)
        return appendExceptionally(future)
    }

    override fun <U : Any?> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>
    ): CloudCompletableFuture<Void> {
        val future = super.thenAcceptBothAsync(other, action)
        return appendExceptionally(future)
    }

    override fun <U : Any?> thenAcceptBothAsync(
        other: CompletionStage<out U>,
        action: BiConsumer<in T, in U>,
        executor: Executor?
    ): CloudCompletableFuture<Void> {
        val future = super.thenAcceptBothAsync(other, action, executor)
        return appendExceptionally(future)
    }

    override fun runAfterBoth(other: CompletionStage<*>, action: Runnable): CloudCompletableFuture<Void> {
        val future = super.runAfterBoth(other, action)
        return appendExceptionally(future)
    }

    override fun runAfterBothAsync(other: CompletionStage<*>, action: Runnable): CloudCompletableFuture<Void> {
        val future = super.runAfterBothAsync(other, action)
        return appendExceptionally(future)
    }

    override fun runAfterBothAsync(
        other: CompletionStage<*>,
        action: Runnable,
        executor: Executor
    ): CloudCompletableFuture<Void> {
        val future = super.runAfterBothAsync(other, action, executor)
        return appendExceptionally(future)
    }

    override fun <U : Any?> applyToEither(
        other: CompletionStage<out T>,
        fn: Function<in T, U>
    ): CloudCompletableFuture<U> {
        val future = super.applyToEither(other, fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>
    ): CloudCompletableFuture<U> {
        val future = super.applyToEitherAsync(other, fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?> applyToEitherAsync(
        other: CompletionStage<out T>,
        fn: Function<in T, U>,
        executor: Executor?
    ): CloudCompletableFuture<U> {
        val future = super.applyToEitherAsync(other, fn, executor)
        return appendExceptionally(future)
    }

    override fun acceptEither(other: CompletionStage<out T>, action: Consumer<in T>): CloudCompletableFuture<Void> {
        val future = super.acceptEither(other, action)
        return appendExceptionally(future)
    }

    override fun acceptEitherAsync(other: CompletionStage<out T>, action: Consumer<in T>): CloudCompletableFuture<Void> {
        val future = super.acceptEitherAsync(other, action)
        return appendExceptionally(future)
    }

    override fun acceptEitherAsync(
        other: CompletionStage<out T>,
        action: Consumer<in T>,
        executor: Executor
    ): CloudCompletableFuture<Void> {
        val future = super.acceptEitherAsync(other, action, executor)
        return appendExceptionally(future)
    }

    override fun runAfterEither(other: CompletionStage<*>, action: Runnable): CloudCompletableFuture<Void> {
        val future = super.runAfterEither(other, action)
        return appendExceptionally(future)
    }

    override fun runAfterEitherAsync(other: CompletionStage<*>, action: Runnable): CloudCompletableFuture<Void> {
        val future = super.runAfterEitherAsync(other, action)
        return appendExceptionally(future)
    }

    override fun runAfterEitherAsync(
        other: CompletionStage<*>,
        action: Runnable,
        executor: Executor
    ): CloudCompletableFuture<Void> {
        val future = super.runAfterEitherAsync(other, action, executor)
        return appendExceptionally(future)
    }

    override fun <U : Any?> thenCompose(fn: Function<in T, out CompletionStage<U>>): CloudCompletableFuture<U> {
        val future = super.thenCompose(fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?> thenComposeAsync(fn: Function<in T, out CompletionStage<U>>): CloudCompletableFuture<U> {
        val future = super.thenComposeAsync(fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?> thenComposeAsync(
        fn: Function<in T, out CompletionStage<U>>,
        executor: Executor
    ): CloudCompletableFuture<U> {
        val future = super.thenComposeAsync(fn, executor)
        return appendExceptionally(future)
    }

    override fun <U : Any?> handle(fn: BiFunction<in T, Throwable, out U>): CloudCompletableFuture<U> {
        val future = super.handle(fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?> handleAsync(fn: BiFunction<in T, Throwable, out U>): CloudCompletableFuture<U> {
        val future = super.handleAsync(fn)
        return appendExceptionally(future)
    }

    override fun <U : Any?> handleAsync(
        fn: BiFunction<in T, Throwable, out U>,
        executor: Executor
    ): CloudCompletableFuture<U> {
        val future = super.handleAsync(fn, executor)
        return appendExceptionally(future)
    }

    override fun whenComplete(action: BiConsumer<in T, in Throwable>): CloudCompletableFuture<T> {
        val future = super.whenComplete(action)
        return appendExceptionally(future)
    }

    override fun whenCompleteAsync(action: BiConsumer<in T, in Throwable>): CloudCompletableFuture<T> {
        val future = super.whenCompleteAsync(action)
        return appendExceptionally(future)
    }

    override fun whenCompleteAsync(action: BiConsumer<in T, in Throwable>, executor: Executor): CloudCompletableFuture<T> {
        val future = super.whenCompleteAsync(action, executor)
        return appendExceptionally(future)
    }

    override fun toCompletableFuture(): CloudCompletableFuture<T> {
        return this
    }

    override fun obtrudeValue(value: T) {
        super.obtrudeValue(value)
    }

    override fun obtrudeException(ex: Throwable?) {
        super.obtrudeException(ex)
    }

    override fun getNumberOfDependents(): Int {
        return super.getNumberOfDependents()
    }

    override fun copy(): CompletableFuture<T> {
        return super.copy()
    }

    override fun minimalCompletionStage(): CompletionStage<T> {
        return super.minimalCompletionStage()
    }

    override fun completeAsync(supplier: Supplier<out T>, executor: Executor): CloudCompletableFuture<T> {
        val future = super.completeAsync(supplier, executor)
        return appendExceptionally(future)
    }

    override fun completeAsync(supplier: Supplier<out T>): CloudCompletableFuture<T> {
        val future = super.completeAsync(supplier)
        return appendExceptionally(future)
    }

    override fun orTimeout(timeout: Long, unit: TimeUnit?): CloudCompletableFuture<T> {
        val future = super.orTimeout(timeout, unit)
        return appendExceptionally(future)
    }

    override fun completeOnTimeout(value: T, timeout: Long, unit: TimeUnit?): CloudCompletableFuture<T> {
        val future = super.completeOnTimeout(value, timeout, unit)
        return appendExceptionally(future)
    }

    companion object {
        private val ASYNC_POOL = ForkJoinPool.commonPool()

        fun runAsync(runnable: Runnable): CompletableFuture<Unit> {
            return asyncRunStage(ASYNC_POOL, runnable)
        }

        fun runAsync(function: () -> Unit): CompletableFuture<Unit> {
            return asyncRunStage(ASYNC_POOL, function)
        }

        fun <U> supplyAsync(supplier: Supplier<U?>): CloudCompletableFuture<U?> {
            return asyncSupplyStage(ASYNC_POOL, supplier)
        }

        fun <U> supplyAsync(function: () -> U?): CloudCompletableFuture<U?> {
            return asyncSupplyStage(ASYNC_POOL, function)
        }

        private fun <U> asyncSupplyStage(
            executor: Executor,
            supplier: Supplier<U>
        ): CloudCompletableFuture<U> {
            val originException = FutureOriginException()
            val future = CloudCompletableFuture<U>()
            executor.execute {
                try {
                    future.complete(supplier.get())
                } catch (ex: Throwable) {
                    originException.initCause(ex)
                    future.completeExceptionally(originException)
                }
            }
            return future
        }

        private fun asyncRunStage(
            executor: Executor,
            runnable: Runnable
        ): CloudCompletableFuture<Unit> {
            return asyncSupplyStage(executor) { runnable.run() }
        }

        fun <T> completedFuture(value: T): CloudCompletableFuture<T> {
            val future = CloudCompletableFuture<T>()
            future.complete(value)
            return future
        }

        fun <T> failedFuture(exception: Exception): CloudCompletableFuture<T> {
            val future = CloudCompletableFuture<T>()
            future.completeExceptionally(exception)
            return future
        }

    }
}