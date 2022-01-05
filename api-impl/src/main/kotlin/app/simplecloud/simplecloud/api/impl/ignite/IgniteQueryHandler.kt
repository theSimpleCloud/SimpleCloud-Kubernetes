package app.simplecloud.simplecloud.api.impl.ignite

import app.simplecloud.simplecloud.api.utils.NetworkComponent
import java.util.concurrent.CompletableFuture

interface IgniteQueryHandler {

    fun <T> sendQuery(topic: String, message: Any, networkComponent: NetworkComponent): CompletableFuture<T>

}