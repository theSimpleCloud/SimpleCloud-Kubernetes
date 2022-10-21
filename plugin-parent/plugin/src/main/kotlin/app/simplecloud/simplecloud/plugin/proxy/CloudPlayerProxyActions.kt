package app.simplecloud.simplecloud.plugin.proxy

import app.simplecloud.simplecloud.api.player.message.ActionBarConfiguration
import app.simplecloud.simplecloud.api.player.message.MessageConfiguration

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 20.10.22
 * Time: 21:21
 */
interface CloudPlayerProxyActions {

    fun sendMessage(configuration: MessageConfiguration)

    fun sendActionBar(configuration: ActionBarConfiguration)

}