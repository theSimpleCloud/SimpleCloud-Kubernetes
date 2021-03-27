package eu.thesimplecloud.api.process.onlineonfiguration

import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.utils.INameable

/**
 * Created by IntelliJ IDEA.
 * Date: 16.03.2021
 * Time: 19:50
 * @author Frederick Baier
 *
 * Describes how much processes shall be online
 *
 */
interface IProcessesOnlineCountConfiguration : INameable {

    /**
     * Returns the amount of processes that should be online in the moment the method gets called
     * According to the returned value processes will be stopped and started
     */
    fun getOnlineCount(group: ICloudProcessGroup): Int

    /**
     * Returns the start priority used to to determine which process to start next (higher is better)
     */
    fun getStartPriority(group: ICloudProcessGroup): Int

}