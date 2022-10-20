/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.api.impl.player

import app.simplecloud.simplecloud.api.future.failedFuture
import app.simplecloud.simplecloud.api.impl.request.player.CloudPlayerUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionPlayer
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerConnection
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.request.player.CloudPlayerUpdateRequest
import app.simplecloud.simplecloud.api.request.player.PlayerConnectRequest
import app.simplecloud.simplecloud.api.service.CloudProcessService
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
class CloudPlayerImpl constructor(
    private val configuration: CloudPlayerConfiguration,
    private val cloudPlayerService: InternalCloudPlayerService,
    private val processService: CloudProcessService,
    private val permissionFactory: Permission.Factory,
    permissionPlayerFactory: PermissionPlayer.Factory
) : OfflineCloudPlayerImpl(configuration, cloudPlayerService, permissionFactory, permissionPlayerFactory), CloudPlayer {

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
        return this.processService.findByName(connectedServerName)
    }

    override fun getCurrentProxy(): CompletableFuture<CloudProcess> {
        return this.processService.findByName(this.configuration.connectedProxyName)
    }

    override fun getPlayerConnection(): PlayerConnection {
        return this.playerConnection
    }

    override fun toConfiguration(): CloudPlayerConfiguration {
        return this.configuration
    }

    override fun createConnectRequest(process: CloudProcess): PlayerConnectRequest {
        TODO("Not yet implemented")
    }

    override fun createUpdateRequest(): CloudPlayerUpdateRequest {
        return CloudPlayerUpdateRequestImpl(this, this.cloudPlayerService, this.permissionFactory)
    }

    override fun sendMessage(source: Identity, message: Component, type: MessageType) {
        this.cloudPlayerService.sendMessage(this.getUniqueId(), message, type)
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