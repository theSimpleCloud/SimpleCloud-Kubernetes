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

package app.simplecloud.simplecloud.api.impl.messagechannel

import app.simplecloud.simplecloud.api.internal.configutation.ProcessExecuteCommandConfiguration
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.messagechannel.MessageChannel
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * Date: 01.04.22
 * Time: 15:54
 * @author Frederick Baier
 *
 */
@Singleton
class InternalMessageChannelProviderImpl @Inject constructor(
    private val messageChannelManager: MessageChannelManager
) : InternalMessageChannelProvider {

    override fun getInternalPlayerLoginChannel(): MessageChannel<PlayerConnectionConfiguration, CloudPlayerConfiguration> {
        return this.messageChannelManager.getOrCreateMessageChannel("internal_player_login")
    }

    override fun getInternalStartProcessChannel(): MessageChannel<ProcessStartConfiguration, CloudProcessConfiguration> {
        return this.messageChannelManager.getOrCreateMessageChannel("internal_process_start")
    }

    override fun getInternalUpdateGroupChannel(): MessageChannel<AbstractCloudProcessGroupConfiguration, Unit> {
        return this.messageChannelManager.getOrCreateMessageChannel("internal_group_update")
    }

    override fun getInternalDeleteGroupChannel(): MessageChannel<String, Unit> {
        return this.messageChannelManager.getOrCreateMessageChannel("internal_group_delete")
    }

    override fun getInternalUpdatePermissionGroupChannel(): MessageChannel<PermissionGroupConfiguration, Unit> {
        return this.messageChannelManager.getOrCreateMessageChannel("internal_permission_group_update")
    }

    override fun getInternalDeletePermissionGroupChannel(): MessageChannel<String, Unit> {
        return this.messageChannelManager.getOrCreateMessageChannel("internal_permission_group_delete")
    }

    override fun getInternalExecuteCommandChannel(): MessageChannel<ProcessExecuteCommandConfiguration, Unit> {
        return this.messageChannelManager.getOrCreateMessageChannel("internal_process_exec")
    }

    override fun getInternalProcessLogsMessageChannel(): MessageChannel<String, List<String>> {
        return this.messageChannelManager.getOrCreateMessageChannel("internal_process_logs")
    }


}