package eu.thesimplecloud.api.messagechannel

import eu.thesimplecloud.api.utils.INetworkComponent

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 08:28
 * @author Frederick Baier
 *
 * Represents a listener for message channel messages
 *
 */
interface IMessageChannelListener<T : Any> {

    /**
     * Called when the message channel this listener is registered to receives a message
     * @param message the received message
     * @param sender the sender of the message
     */
    fun messageReceived(message: T, sender: INetworkComponent)

}