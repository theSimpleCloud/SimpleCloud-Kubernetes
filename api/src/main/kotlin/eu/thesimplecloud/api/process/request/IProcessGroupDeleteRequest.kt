package eu.thesimplecloud.api.process.request

import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.utils.IRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 18.03.2021
 * Time: 15:18
 * @author Frederick Baier
 */
interface IProcessGroupDeleteRequest : IRequest<Void> {

    /**
     * Returns the process group this request will start a process of
     */
    fun getProcessGroup(): ICloudProcessGroup

}