package eu.thesimplecloud.api.process.request

import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.utils.IRequest

/**
 * Created by IntelliJ IDEA.
 * Date: 18.03.2021
 * Time: 14:54
 * @author Frederick Baier
 *
 * Request to stop a process
 *
 */
interface IProcessStopRequest : IRequest<Void> {

    /**
     * Returns the process to be stopped
     */
    fun getProcess(): ICloudProcess

}