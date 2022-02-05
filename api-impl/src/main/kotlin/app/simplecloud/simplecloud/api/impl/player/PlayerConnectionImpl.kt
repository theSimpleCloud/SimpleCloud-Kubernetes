package app.simplecloud.simplecloud.api.impl.player

import app.simplecloud.simplecloud.api.player.PlayerConnection
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.utils.Address
import java.util.*

/**
 * Date: 07.01.22
 * Time: 18:00
 * @author Frederick Baier
 *
 */
class PlayerConnectionImpl(
    private val configuration: PlayerConnectionConfiguration
) : PlayerConnection {

    override fun getUniqueId(): UUID {
        return this.configuration.uniqueId
    }

    override fun getAddress(): Address {
        return this.configuration.address
    }

    override fun isOnlineMode(): Boolean {
        return this.configuration.onlineMode
    }

    override fun getVersion(): Int {
        return this.configuration.numericalClientVersion
    }

    override fun getName(): String {
        return this.configuration.name
    }

    override fun toConfiguration(): PlayerConnectionConfiguration {
        return this.configuration
    }
}