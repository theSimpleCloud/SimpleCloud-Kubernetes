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

package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.node.messagechannel.*
import com.google.inject.Inject
import com.google.inject.Injector

class MessageChannelsInitializer @Inject constructor(
    private val messageChannelManager: MessageChannelManager,
    private val injector: Injector
) {

    fun initializeMessageChannels() {
        registerPermissionGroupDeleteMessageChannel()
        registerPermissionGroupUpdateMessageChannel()
        registerGroupDeleteMessageChannel()
        registerGroupUpdateMessageChannel()
        registerStartProcessMessageChannel()
        registerPlayerLoginMessageChannel()
    }

    private fun registerPlayerLoginMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<PlayerConnectionConfiguration, CloudPlayerConfiguration>("internal_player_login")
        val messageHandler = this.injector.getInstance(CloudPlayerLoginMessageHandler::class.java)
        messageChannel.setMessageHandler(messageHandler)
    }

    private fun registerStartProcessMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<ProcessStartConfiguration, CloudProcessConfiguration>("internal_start_process")
        val startProcessMessageHandler = this.injector.getInstance(StartProcessMessageHandler::class.java)
        messageChannel.setMessageHandler(startProcessMessageHandler)
    }

    private fun registerGroupUpdateMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<AbstractCloudProcessGroupConfiguration, Unit>("internal_update_group")
        val updateGroupMessageHandler = this.injector.getInstance(UpdateGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(updateGroupMessageHandler)
    }

    private fun registerGroupDeleteMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<String, Unit>("internal_delete_group")
        val deleteGroupMessageHandler = this.injector.getInstance(DeleteGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(deleteGroupMessageHandler)
    }

    private fun registerPermissionGroupUpdateMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<PermissionGroupConfiguration, Unit>("internal_update_permission_group")
        val updateGroupMessageHandler = this.injector.getInstance(UpdatePermissionGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(updateGroupMessageHandler)
    }

    private fun registerPermissionGroupDeleteMessageChannel() {
        val messageChannel = this.messageChannelManager
            .registerMessageChannel<String, Unit>("internal_delete_permission_group")
        val deleteGroupMessageHandler = this.injector.getInstance(DeletePermissionGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(deleteGroupMessageHandler)
    }

}