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

package app.simplecloud.simplecloud.api.internal.messagechannel

import app.simplecloud.simplecloud.api.internal.configutation.PlayerLoginConfiguration
import app.simplecloud.simplecloud.api.internal.configutation.ProcessExecuteCommandConfiguration
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.messagechannel.MessageChannel
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import java.util.*

/**
 * Date: 01.04.22
 * Time: 15:48
 * @author Frederick Baier
 *
 */
interface InternalMessageChannelProvider {

    fun getInternalPlayerLoginChannel(): MessageChannel<PlayerLoginConfiguration, CloudPlayerConfiguration>

    fun getInternalPlayerDisconnectChannel(): MessageChannel<UUID, Unit>

    fun getInternalStartProcessChannel(): MessageChannel<ProcessStartConfiguration, CloudProcessConfiguration>

    fun getInternalUpdateGroupChannel(): MessageChannel<AbstractProcessTemplateConfiguration, Unit>

    fun getInternalDeleteGroupChannel(): MessageChannel<String, Unit>

    fun getInternalUpdateStaticTemplateChannel(): MessageChannel<AbstractProcessTemplateConfiguration, Unit>

    fun getInternalDeleteStaticTemplateChannel(): MessageChannel<String, Unit>

    fun getInternalUpdatePermissionGroupChannel(): MessageChannel<PermissionGroupConfiguration, Unit>

    fun getInternalDeletePermissionGroupChannel(): MessageChannel<String, Unit>

    fun getInternalExecuteCommandChannel(): MessageChannel<ProcessExecuteCommandConfiguration, Unit>

    fun getInternalProcessLogsMessageChannel(): MessageChannel<String, List<String>>

    fun getInternalGetOfflineCloudPlayerByNameChannel(): MessageChannel<String, OfflineCloudPlayerConfiguration>

    fun getInternalGetOfflineCloudPlayerByUUIDChannel(): MessageChannel<UUID, OfflineCloudPlayerConfiguration>

    fun getInternalOfflinePlayerUpdateChannel(): MessageChannel<OfflineCloudPlayerConfiguration, Unit>

}