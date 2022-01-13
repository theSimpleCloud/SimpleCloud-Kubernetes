package app.simplecloud.simplecloud.api.impl.player

import app.simplecloud.simplecloud.api.player.OfflineCloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration

/**
 * Date: 07.01.22
 * Time: 18:22
 * @author Frederick Baier
 *
 */
interface OfflineCloudPlayerFactory {

    fun create(configuration: OfflineCloudPlayerConfiguration): OfflineCloudPlayer

}