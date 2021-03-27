package eu.thesimplecloud.api.player.update

import eu.thesimplecloud.api.utils.IRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 18:38
 * @author Frederick Baier
 */
interface IOfflinePlayerUpdateRequest : IRequest<Void> {

    /**
     * Sets the display name of the player
     */
    fun setDisplayName(name: String)

}