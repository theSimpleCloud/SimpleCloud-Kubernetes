package eu.thesimplecloud.api.player

import eu.thesimplecloud.api.text.CloudText
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 26.03.2021
 * Time: 18:42
 * @author Frederick Baier
 */
interface ICloudPlayerMessageAdapter {

    /**
     * Sends the [cloudText] to this player
     * @return a future that completes when the the message was sent
     */
    fun sendMessage(cloudText: CloudText): CompletableFuture<Void>

    /**
     * Sends the [message] to this player
     * @return a future that completes when the the message was sent
     */
    fun sendMessage(message: String): CompletableFuture<Void> {
        return sendMessage(CloudText(message))
    }


}