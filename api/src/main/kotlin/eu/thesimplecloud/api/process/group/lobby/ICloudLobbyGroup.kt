package eu.thesimplecloud.api.process.group.lobby

import eu.thesimplecloud.api.process.group.server.ICloudServerGroup
import eu.thesimplecloud.api.process.group.update.ICloudLobbyGroupUpdateRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 21:16
 * @author Frederick Baier
 */
interface ICloudLobbyGroup : ICloudServerGroup {

    fun getLobbyPriority(): Int

    override fun createUpdateRequest(): ICloudLobbyGroupUpdateRequest

}