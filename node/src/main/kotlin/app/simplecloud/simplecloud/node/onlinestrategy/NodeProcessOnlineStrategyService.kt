package app.simplecloud.simplecloud.node.onlinestrategy

import app.simplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountStrategy
import java.util.concurrent.CompletableFuture

/**
 * Date: 25.03.22
 * Time: 08:43
 * @author Frederick Baier
 *
 */
interface NodeProcessOnlineStrategyService {

    /**
     * Starts and stops processes by comparing the current number of online processes to the desired one
     */
    fun checkProcessOnlineCount()

    /**
     * Returns the [ProcessesOnlineCountStrategy] found by the specified [name] or a default config
     */
    fun getByProcessGroupName(name: String): CompletableFuture<ProcessesOnlineCountStrategy>

}