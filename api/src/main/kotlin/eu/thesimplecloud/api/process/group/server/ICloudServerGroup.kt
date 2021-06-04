package eu.thesimplecloud.api.process.group.server

import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.group.update.ICloudServerGroupUpdateRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 21:20
 * @author Frederick Baier
 */
interface ICloudServerGroup : ICloudProcessGroup {

    override fun createUpdateRequest(): ICloudServerGroupUpdateRequest

}