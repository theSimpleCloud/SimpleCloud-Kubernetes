package app.simplecloud.simplecloud.plugin.proxy.type.bungee

import app.simplecloud.simplecloud.api.player.message.ActionBarConfiguration
import app.simplecloud.simplecloud.api.player.message.MessageConfiguration
import app.simplecloud.simplecloud.plugin.proxy.CloudPlayerProxyActions
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import java.util.function.Supplier

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 20.10.22
 * Time: 21:30
 */
class CloudPlayerBungeeActions(
    private val bungeeAudienceSupplier: Supplier<BungeeAudiences>
): CloudPlayerProxyActions {

    override fun sendMessage(configuration: MessageConfiguration) {
        val player = bungeeAudienceSupplier.get().player(configuration.uniqueId)
        player.sendMessage(configuration.message, configuration.type)
    }

    override fun sendActionBar(configuration: ActionBarConfiguration) {
        val player = bungeeAudienceSupplier.get().player(configuration.uniqueId)
        player.sendActionBar(configuration.message)
    }

}