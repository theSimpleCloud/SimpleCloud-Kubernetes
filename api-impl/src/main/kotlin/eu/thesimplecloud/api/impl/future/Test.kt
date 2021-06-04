package eu.thesimplecloud.api.impl.future

import eu.thesimplecloud.api.impl.future.exception.CompletedWithNullException
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 29.03.2021
 * Time: 23:53
 * @author Frederick Baier
 */

fun main() {
    val future = CompletableFuture.completedFuture<Int>(null)
    println(future.get() ?: "null")
    val future2 = future.thenApply { it ?: throw CompletedWithNullException() }
    future2.handle { int, throwable ->
        println("handle")
        println(int ?: "int was null")
        println(throwable::class.java)
    }
    future.thenAccept { println(it ?: "null22") }
    future.nonNull()
}