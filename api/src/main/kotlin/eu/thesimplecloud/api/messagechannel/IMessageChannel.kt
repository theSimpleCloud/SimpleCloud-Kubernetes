package eu.thesimplecloud.api.messagechannel

import eu.thesimplecloud.api.messagechannel.handler.IMessageHandler
import eu.thesimplecloud.api.utils.INameable
import eu.thesimplecloud.api.utils.INetworkComponent

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 08:26
 * @author Frederick Baier
 *
 * @param T the type to be sent
 * @param R the type to be received
 *
 */
interface IMessageChannel<T : Any, R: Any> : INameable {

    /**
     * Creates a message request
     * @param message the message to be sent
     * @param receiver the receiver of the message
     * @return the created message request with the expected response type
     */
    fun createMessageRequest(message: T, receiver: INetworkComponent): IMessageRequest<R>

    /**
     * Creates a message request
     * @param message the message to be sent
     * @param receivers the receivers of the message
     * @return the created message request with no response
     */
    fun createMessageRequest(message: T, receivers: List<INetworkComponent>): IMessageRequest<Unit>

    /**
     * Sets the message handler to be used for incoming messages
     * The handler is notified every time a message is received
     * @param handler the message handler to handle icoming messages
     */
    fun setMessageHandler(handler: IMessageHandler<T, R>)

    /**
     * Handles a request
     */
    fun handleRequest(message: T, sender: INetworkComponent): R?

}