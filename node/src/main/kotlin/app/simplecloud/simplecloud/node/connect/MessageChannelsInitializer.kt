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

import app.simplecloud.simplecloud.api.internal.InternalCloudAPI
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.node.messagechannel.*

class MessageChannelsInitializer(
    private val cloudAPI: InternalCloudAPI,
    private val internalMessageChannelProvider: InternalMessageChannelProvider
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
        val messageHandler = ProcessLogsMessageHandler(this.cloudAPI.getProcessService())
        messageChannel.setMessageHandler(messageHandler)
    }


    private fun registerProcessExecuteCommandMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalExecuteCommandChannel()
        val messageHandler = ProcessExecuteCommandMessageHandler(this.cloudAPI.getProcessService())
        messageChannel.setMessageHandler(messageHandler)
    }

    private fun registerPlayerLoginMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalPlayerLoginChannel()
        val messageHandler = CloudPlayerLoginMessageHandler(this.cloudAPI.getCloudPlayerService())
        messageChannel.setMessageHandler(messageHandler)
    }

    private fun registerStartProcessMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalStartProcessChannel()
        val startProcessMessageHandler = StartProcessMessageHandler(this.cloudAPI.getProcessService())
        messageChannel.setMessageHandler(startProcessMessageHandler)
    }

    private fun registerGroupUpdateMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalUpdateGroupChannel()
        val updateGroupMessageHandler = UpdateGroupMessageHandler(this.cloudAPI.getProcessGroupService())
        messageChannel.setMessageHandler(updateGroupMessageHandler)
    }

    private fun registerGroupDeleteMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalDeleteGroupChannel()
        val deleteGroupMessageHandler = DeleteGroupMessageHandler(this.cloudAPI.getProcessGroupService())
        messageChannel.setMessageHandler(deleteGroupMessageHandler)
    }

    private fun registerPermissionGroupUpdateMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalUpdatePermissionGroupChannel()
        val updateGroupMessageHandler = UpdatePermissionGroupMessageHandler(this.cloudAPI.getPermissionGroupService())
        messageChannel.setMessageHandler(updateGroupMessageHandler)
    }

    private fun registerPermissionGroupDeleteMessageChannel() {
        val messageChannel = this.internalMessageChannelProvider.getInternalDeletePermissionGroupChannel()
        val deleteGroupMessageHandler = DeletePermissionGroupMessageHandler(this.cloudAPI.getPermissionGroupService())
        messageChannel.setMessageHandler(deleteGroupMessageHandler)
    }

}