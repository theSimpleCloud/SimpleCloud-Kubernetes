package app.simplecloud.simplecloud.api.impl.player

import app.simplecloud.simplecloud.api.future.failedFuture
import app.simplecloud.simplecloud.api.impl.request.player.CloudPlayerUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerConnection
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.request.player.CloudPlayerUpdateRequest
import app.simplecloud.simplecloud.api.request.player.PlayerConnectRequest
import app.simplecloud.simplecloud.api.service.CloudProcessService
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.TitlePart
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.01.22
 * Time: 00:36
 * @author Frederick Baier
 *
 */
class CloudPlayerImpl @Inject constructor(
    @Assisted private val configuration: CloudPlayerConfiguration,
    private val cloudPlayerService: InternalCloudPlayerService,
    private val processService: CloudProcessService,
) : OfflineCloudPlayerImpl(configuration, cloudPlayerService), CloudPlayer {

    private val playerConnection = PlayerConnectionImpl(this.configuration.lastPlayerConnection)

    override fun getCurrentServerName(): String? {
        return this.configuration.connectedServerName
    }

    override fun getCurrentProxyName(): String {
        return this.configuration.connectedProxyName
    }


    override fun getCurrentServer(): CompletableFuture<CloudProcess> {
        val connectedServerName = this.configuration.connectedServerName
            ?: return failedFuture(NullPointerException("Connected-Server not set"))
        return this.processService.findProcessByName(connectedServerName)
    }

    override fun getCurrentProxy(): CompletableFuture<CloudProcess> {
        return this.processService.findProcessByName(this.configuration.connectedProxyName)
    }

    override fun getPlayerConnection(): PlayerConnection {
        return this.playerConnection
    }

    override fun createConnectRequest(process: CloudProcess): PlayerConnectRequest {
        TODO("Not yet implemented")
    }

    override fun createUpdateRequest(): CloudPlayerUpdateRequest {
        return CloudPlayerUpdateRequestImpl(this, this.cloudPlayerService)
    }

    override fun sendMessage(source: Identity, message: Component, type: MessageType) {

    }

    override fun sendActionBar(message: Component) {

    }

    override fun sendPlayerListHeaderAndFooter(header: Component, footer: Component) {

    }

    override fun <T> sendTitlePart(part: TitlePart<T>, value: T) {

    }

    override fun clearTitle() {

    }

    override fun resetTitle() {

    }

    override fun showBossBar(bar: BossBar) {

    }

    override fun hideBossBar(bar: BossBar) {

    }

    override fun playSound(sound: Sound) {

    }

    override fun playSound(sound: Sound, x: Double, y: Double, z: Double) {

    }

    override fun playSound(sound: Sound, emitter: Sound.Emitter) {

    }

    override fun stopSound(stop: SoundStop) {

    }

    override fun openBook(book: Book) {
        
    }

}