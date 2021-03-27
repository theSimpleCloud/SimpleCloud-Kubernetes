package eu.thesimplecloud.api.messagechannel

import eu.thesimplecloud.api.utils.INameable
import eu.thesimplecloud.api.utils.INetworkComponent

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 08:26
 * @author Frederick Baier
 */
interface IMessageChannel<T : Any> : INameable {

    /**
     * Sends the specified message to all components in the network
     * @param message the message to be sent
     */
    fun sendMessageToAllComponents(message: T)

    /**
     * Sends a message to the receivers
     * @param message the message to be sent
     * @param receivers the receiver components that shall receive the message
     */
    fun sendMessage(message: T, receivers: List<INetworkComponent>)

    /**
     * Sends the message to the receiver
     * @param message the message to be sent
     * @param receiver the receiver component that shall receive the message
     */
    fun sendMessage(message: T, receiver: INetworkComponent)

    /**
     * Registers a listener
     * The listener is getting notified for every message received
     * @param listener the listener to be registered
     */
    fun registerListener(listener: IMessageChannelListener<T>)

    /**
     * Unregisters a listener
     * The listener will no longer get notified
     * If the listener is not registered nothing happens
     * @param listener the listener to be unregistered
     */
    fun unregisterListener(listener: IMessageChannelListener<T>)

}