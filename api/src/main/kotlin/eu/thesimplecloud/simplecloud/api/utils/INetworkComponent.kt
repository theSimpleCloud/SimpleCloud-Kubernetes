package eu.thesimplecloud.simplecloud.api.utils

import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 08:25
 * @author Frederick Baier
 *
 * Represents a component in the cluster (process or node)
 *
 */
interface INetworkComponent : INameable {

    /**
     * Returns the ignite unique id of this component
     */
    fun getIgniteId(): UUID

}