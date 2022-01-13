package app.simplecloud.simplecloud.api.impl.player

import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration

/**
 * Date: 07.01.22
 * Time: 18:22
 * @author Frederick Baier
 *
 */
interface CloudPlayerFactory {

    fun create(configuration: CloudPlayerConfiguration): CloudPlayer

}