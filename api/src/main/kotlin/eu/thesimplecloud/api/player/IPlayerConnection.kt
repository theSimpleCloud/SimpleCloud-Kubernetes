package eu.thesimplecloud.api.player

import eu.thesimplecloud.api.utils.Address
import eu.thesimplecloud.api.utils.INameable
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 15:33
 * @author Frederick Baier
 */
interface IPlayerConnection : INameable {

    /**
     * Returns the unique id of this player
     */
    fun getUniqueId(): UUID


    /**
     * Returns the address of this player
     */
    fun getAddress(): Address

    /**
     * Returns whether the player's connection is in online mode
     */
    fun isOnlineMode(): Boolean

    /**
     * Get the numerical client version of the player
     * @return the protocol version of the client
     */
    fun getVersion(): Int

}