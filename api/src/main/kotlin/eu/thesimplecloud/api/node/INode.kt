package eu.thesimplecloud.api.node

import eu.thesimplecloud.api.utils.Address
import eu.thesimplecloud.api.utils.INetworkComponent
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 08:20
 * @author Frederick Baier
 *
 * Represents a node
 *
 */
interface INode : INetworkComponent {

    /**
     * Returns the address of the node
     */
    fun getAddress(): Address


}