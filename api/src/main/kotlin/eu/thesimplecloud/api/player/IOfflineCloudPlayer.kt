package eu.thesimplecloud.api.player

import eu.thesimplecloud.api.player.update.IOfflinePlayerUpdateRequest
import eu.thesimplecloud.api.utils.INameable
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 15:32
 * @author Frederick Baier
 */
interface IOfflineCloudPlayer : INameable {

    /**
     * Returns the unique id of the player
     */
    fun getUniqueId(): UUID

    /**
     * Returns the timestamp the player first logged in
     */
    fun getFirstLogin(): Long

    /**
     * Returns the timestamp the player last logged in
     */
    fun getLastLogin(): Long

    /**
     * Returns the time in milliseconds the player was online
     */
    fun getOnlineTime(): Long

    /**
     * Returns the last [IPlayerConnection] the player had
     */
    fun getLastPlayerConnection(): IPlayerConnection

    /**
     * Returns whether the player is online
     */
    fun isOnline(): Boolean

    /**
     * Returns the display name of the player
     */
    fun getDisplayName(): String

    /**
     * Returns a new [IOfflineCloudPlayer] with the data of this player
     */
    fun toOfflinePlayer(): IOfflineCloudPlayer

    /**
     * Creates a request to update this player
     */
    fun createUpdateRequest(): IOfflinePlayerUpdateRequest

}