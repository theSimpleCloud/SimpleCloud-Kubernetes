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

import kotlinx.coroutines.*
import kotlinx.coroutines.CancellationException
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.BiConsumer
import kotlin.coroutines.*


private val threadContext = newCachedThreadPoolContext("cloud")
private val exceptionHandler = CoroutineExceptionHandler { context, exception ->
    println("CoroutineExceptionHandler got $exception")
    println(Thread.currentThread().name)
}

fun newCachedThreadPoolContext(name: String): ExecutorCoroutineDispatcher {
    val threadNo = AtomicInteger()
    val executor = Executors.newCachedThreadPool { runnable ->
        val t = Thread(runnable, name + "-" + threadNo.incrementAndGet())
        t.isDaemon = true
        t
    }
    return executor.asCoroutineDispatcher()
}

val CloudScope = CoroutineScope(threadContext + exceptionHandler + SupervisorJob())

/**
 * Starts a new coroutine and returns its result as an implementation of [CompletableFuture].
 * The running coroutine is cancelled when the resulting future is cancelled or otherwise completed.
 *
 * The coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with the [context] argument.
 * If the context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * The parent job is inherited from a [CoroutineScope] as well, but it can also be overridden
 * with corresponding [context] element.
 *
 * By default, the coroutine is immediately scheduled for execution.
 * Other options can be specified via `start` parameter. See [CoroutineStart] for details.
 * A value of [CoroutineStart.LAZY] is not supported
 * (since `CompletableFuture` framework does not provide the corresponding capability) and
 * produces [IllegalArgumentException].
 *
 * See [newCoroutineContext][CoroutineScope.newCoroutineContext] for a description of debugging facilities that are available for newly created coroutine.
 *
 * @param context additional to [CoroutineScope.coroutineContext] context of the coroutine.
 * @param start coroutine start option. The default value is [CoroutineStart.DEFAULT].
 * @param block the coroutine code.
 */
@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
public fun <T> CoroutineScope.future(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
) : CompletableFuture<T> {
    require(!start.isLazy) { "$start start is not supported" }
    val newContext = this.newCoroutineContext(context)
    val future = CloudCompletableFuture<T>()
    val coroutine = CompletableFutureCoroutine(newContext, future)
    future.whenComplete(coroutine) // Cancel coroutine if future was completed externally
    coroutine.start(start, coroutine, block)
    return future
}

@OptIn(InternalCoroutinesApi::class)
private class CompletableFutureCoroutine<T>(
    context: CoroutineContext,
    private val future: CompletableFuture<T>
) : AbstractCoroutine<T>(context, initParentJob = true, active = true), BiConsumer<T?, Throwable?> {
    override fun accept(value: T?, exception: Throwable?) {
        cancel()
    }

    override fun onCompleted(value: T) {
        future.complete(value)
    }

    override fun onCancelled(cause: Throwable, handled: Boolean) {
        if (!future.completeExceptionally(cause) && !handled) {
            // prevents loss of exception that was not handled by parent & could not be set to CompletableFuture
            handleCoroutineException(context, cause)
        }
    }
}

/**
 * Converts this deferred value to the instance of [CompletableFuture].
 * The deferred value is cancelled when the resulting future is cancelled or otherwise completed.
 */
public fun <T> Deferred<T>.asCompletableFuture(): CompletableFuture<T> {
    val future = CloudCompletableFuture<T>()
    setupCancellation(future)
    invokeOnCompletion {
        try {
            future.complete(getCompleted())
        } catch (t: Throwable) {
            future.completeExceptionally(t)
        }
    }
    return future
}

/**
 * Converts this job to the instance of [CompletableFuture].
 * The job is cancelled when the resulting future is cancelled or otherwise completed.
 */
public fun Job.asCompletableFuture(): CompletableFuture<Unit> {
    val future = CloudCompletableFuture<Unit>()
    setupCancellation(future)
    invokeOnCompletion { cause ->
        if (cause === null) future.complete(Unit)
        else future.completeExceptionally(cause)
    }
    return future
}

private fun Job.setupCancellation(future: CompletableFuture<*>) {
    future.whenComplete { _, exception ->
        cancel(exception?.let {
            it as? CancellationException ?: CancellationException("CompletableFuture was completed exceptionally", it)
        })
    }
}

/**
 * Converts this [CompletionStage] to an instance of [Deferred].
 *
 * The [CompletableFuture] that corresponds to this [CompletionStage] (see [CompletionStage.toCompletableFuture])
 * is cancelled when the resulting deferred is cancelled.
 */
@OptIn(InternalCoroutinesApi::class)
@Suppress("DeferredIsResult")
public fun <T> CompletionStage<T>.asDeferred(): Deferred<T> {
    val future = toCompletableFuture() // retrieve the future
    // Fast path if already completed
    if (future.isDone) {
        return try {
            @Suppress("UNCHECKED_CAST")
            (CompletableDeferred(future.get() as T))
        } catch (e: Throwable) {
            // unwrap original cause from ExecutionException
            val original = (e as? ExecutionException)?.cause ?: e
            CompletableDeferred<T>().also { it.completeExceptionally(original) }
        }
    }
    val result = CompletableDeferred<T>()
    whenComplete { value, exception ->
        try {
            if (exception == null) {
                // the future has completed normally
                result.complete(value)
            } else {
                // the future has completed with an exception, unwrap it consistently with fast path
                // Note: In the fast-path the implementation of CompletableFuture.get() does unwrapping
                result.completeExceptionally((exception as? CompletionException)?.cause ?: exception)
            }
        } catch (e: Throwable) {
            // We come here iff the internals of Deferred threw an exception during its completion
            handleCoroutineException(EmptyCoroutineContext, e)
        }
    }
    result.cancelFutureOnCompletion(future)
    return result
}

/**
 * Awaits for completion of [CompletionStage] without blocking a thread.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this function
 * stops waiting for the completion stage and immediately resumes with [CancellationException][kotlinx.coroutines.CancellationException].
 *
 * This method is intended to be used with one-shot futures, so on coroutine cancellation the [CompletableFuture] that
 * corresponds to this [CompletionStage] (see [CompletionStage.toCompletableFuture])
 * is cancelled. If cancelling the given stage is undesired, `stage.asDeferred().await()` should be used instead.
 */
public suspend fun <T> CompletionStage<T>.await(): T {
    val future = toCompletableFuture() // retrieve the future
    // fast path when CompletableFuture is already done (does not suspend)
    if (future.isDone) {
        try {
            @Suppress("UNCHECKED_CAST", "BlockingMethodInNonBlockingContext")
            return future.get() as T
        } catch (e: ExecutionException) {
            throw unwrapFutureException(e) // unwrap original cause from ExecutionException
        }
    }
    // slow path -- suspend
    return suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
        val consumer = ContinuationConsumer(cont)
        this.whenComplete(consumer)
        cont.invokeOnCancellation {
            future.cancel(false)
            consumer.cont = null // shall clear reference to continuation to aid GC
        }
    }
}

private class ContinuationConsumer<T>(
    @Volatile @JvmField var cont: Continuation<T>?
) : BiConsumer<T?, Throwable?> {
    @Suppress("UNCHECKED_CAST")
    override fun accept(result: T?, exception: Throwable?) {
        val cont = this.cont ?: return // atomically read current value unless null
        if (exception == null) {
            // the future has completed normally
            cont.resume(result as T)
        } else {
            // the future has completed with an exception, unwrap it to provide consistent view of .await() result and to propagate only original exception
            cont.resumeWithException(unwrapFutureException(exception))
        }
    }
}
