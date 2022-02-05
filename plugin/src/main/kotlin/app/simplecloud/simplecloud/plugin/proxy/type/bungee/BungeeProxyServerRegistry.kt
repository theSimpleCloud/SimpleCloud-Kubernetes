package app.simplecloud.simplecloud.plugin.proxy.type.bungee

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.group.ProcessGroupType
import app.simplecloud.simplecloud.plugin.proxy.ProxyServerRegistry
import com.google.inject.Inject
import com.google.inject.Singleton
import net.md_5.bungee.api.ProxyServer
import java.net.InetSocketAddress
import java.util.*

/**
 * Date: 24.01.22
 * Time: 19:01
 * @author Frederick Baier
 *
 */
@Singleton
class BungeeProxyServerRegistry @Inject constructor(
    private val proxyServer: ProxyServer
) : ProxyServerRegistry {

    override fun registerProcess(cloudProcess: CloudProcess) {
        if (this.proxyServer.getServerInfo(cloudProcess.getName()) != null) {
            throw IllegalArgumentException("Service is already registered!")
        }

        if (cloudProcess.getProcessType() == ProcessGroupType.PROXY)
            return

        println("Registered process ${cloudProcess.getName()}")
        val address = cloudProcess.getAddress()
        val socketAddress = InetSocketAddress(address.host, address.port)
        registerServer(cloudProcess.getName(), cloudProcess.getUniqueId(), socketAddress)
    }

    override fun registerServer(name: String, uniqueId: UUID, socketAddress: InetSocketAddress) {
        val info = ProxyServer.getInstance().constructServerInfo(
            name,
            socketAddress,
            uniqueId.toString(),
            false
        )
        ProxyServer.getInstance().servers[name] = info
    }

    override fun unregisterServer(name: String) {
        this.proxyServer.servers.remove(name)
    }
}