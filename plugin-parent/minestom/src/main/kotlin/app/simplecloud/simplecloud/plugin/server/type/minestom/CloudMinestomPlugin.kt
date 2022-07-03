package app.simplecloud.simplecloud.plugin.server.type.minestom

import app.simplecloud.simplecloud.api.impl.env.RealEnvironmentVariables
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.distribution.hazelcast.HazelcastDistributionFactory
import app.simplecloud.simplecloud.plugin.SelfOnlineCountProvider
import app.simplecloud.simplecloud.plugin.server.CloudServerPlugin
import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 01.07.22
 * Time: 18:42
 */
class CloudMinestomPlugin : Extension() {

    private val cloudPlugin = CloudServerPlugin(
        HazelcastDistributionFactory(),
        RealEnvironmentVariables(),
        Address.fromIpString("distribution:1670"),
        SelfOnlineCountProvider { MinecraftServer.getConnectionManager().onlinePlayers.size }
    )

    override fun initialize() {
        MinestomListener(this.cloudPlugin.onlineCountUpdater, this.eventNode).listen()
    }

    override fun terminate() {
    }

}