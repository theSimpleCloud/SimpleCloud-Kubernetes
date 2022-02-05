package app.simplecloud.simplecloud.plugin.proxy

import app.simplecloud.simplecloud.api.process.CloudProcess
import java.net.InetSocketAddress
import java.util.*

/**
 * Date: 24.01.22
 * Time: 18:59
 * @author Frederick Baier
 *
 * Registers and unregisters processes on the current proxy
 */
interface ProxyServerRegistry {

    fun registerProcess(cloudProcess: CloudProcess)


    fun registerServer(name: String, uniqueId: UUID, socketAddress: InetSocketAddress)


    fun unregisterServer(name: String)

}