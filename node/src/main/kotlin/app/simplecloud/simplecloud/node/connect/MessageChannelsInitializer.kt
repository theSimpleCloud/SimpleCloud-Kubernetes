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

import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.node.messagechannel.*
import com.google.inject.Inject
import com.google.inject.Injector

class MessageChannelsInitializer @Inject constructor(
    private val internalMessageChannelProvider: InternalMessageChannelProvider,
    private val injector: Injector
) {

    fun initializeMessageChannels() {
        registerPermissionGroupDeleteMessageChannel()
        registerPermissionGroupUpdateMessageChannel()
        registerGroupDeleteMessageChannel()
        registerGroupUpdateMessageChannel()
        registerStartProcessMessageChannel()
        registerPlayerLoginMessageChannel()
        registerProcessExecuteCommandMessageChannel()
        registerProcessLogsMessageChannel()
    }

    private fun registerProcessLogsMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalProcessLogsMessageChannel()
        val messageHandler = this.injector.getInstance(ProcessLogsMessageHandler::class.java)
        messageChannel.setMessageHandler(messageHandler)
    }


    private fun registerProcessExecuteCommandMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalExecuteCommandChannel()
        val messageHandler = this.injector.getInstance(ProcessExecuteCommandMessageHandler::class.java)
        messageChannel.setMessageHandler(messageHandler)
    }

    private fun registerPlayerLoginMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalPlayerLoginChannel()
        val messageHandler = this.injector.getInstance(CloudPlayerLoginMessageHandler::class.java)
        messageChannel.setMessageHandler(messageHandler)
    }

    private fun registerStartProcessMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalStartProcessChannel()
        val startProcessMessageHandler = this.injector.getInstance(StartProcessMessageHandler::class.java)
        messageChannel.setMessageHandler(startProcessMessageHandler)
    }

    private fun registerGroupUpdateMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalUpdateGroupChannel()
        val updateGroupMessageHandler = this.injector.getInstance(UpdateGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(updateGroupMessageHandler)
    }

    private fun registerGroupDeleteMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalDeleteGroupChannel()
        val deleteGroupMessageHandler = this.injector.getInstance(DeleteGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(deleteGroupMessageHandler)
    }

    private fun registerPermissionGroupUpdateMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalUpdatePermissionGroupChannel()
        val updateGroupMessageHandler = this.injector.getInstance(UpdatePermissionGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(updateGroupMessageHandler)
    }

    private fun registerPermissionGroupDeleteMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalDeletePermissionGroupChannel()
        val deleteGroupMessageHandler = this.injector.getInstance(DeletePermissionGroupMessageHandler::class.java)
        messageChannel.setMessageHandler(deleteGroupMessageHandler)
    }

}