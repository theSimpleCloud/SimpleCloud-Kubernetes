package eu.thesimplecloud.api.process.group.proxy

import eu.thesimplecloud.api.process.group.ICloudProcessGroup

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 21:20
 * @author Frederick Baier
 */
interface ICloudProxyGroup : ICloudProcessGroup {

    fun getStartPort(): Int

}