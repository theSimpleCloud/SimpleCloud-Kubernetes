package eu.thesimplecloud.api.player

import eu.thesimplecloud.api.player.request.IPlayerConnectRequest
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.text.CloudText
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 21.03.2021
 * Time: 15:29
 * @author Frederick Baier
 */
interface ICloudPlayer : IOfflineCloudPlayer, ICloudPlayerMessageAdapter {

    fun getCurrentServer(): ICloudProcess

    fun getCurrentProxy(): ICloudProcess

    fun getPlayerConnection(): IPlayerConnection

    override fun getLastPlayerConnection(): IPlayerConnection {
        return getPlayerConnection()
    }

    fun createConnectRequest(process: ICloudProcess): IPlayerConnectRequest


}