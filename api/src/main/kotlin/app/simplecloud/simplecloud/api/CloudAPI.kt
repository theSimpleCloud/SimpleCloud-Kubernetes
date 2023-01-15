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

package app.simplecloud.simplecloud.api

import app.simplecloud.simplecloud.api.cache.CacheHandler
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.service.*
import app.simplecloud.simplecloud.eventapi.EventManager
import java.util.concurrent.CompletableFuture

/**
 * Date: 07.05.22
 * Time: 11:12
 * @author Frederick Baier
 *
 */
interface CloudAPI {

    fun getLocalNetworkComponentName(): String

    fun getProcessGroupService(): CloudProcessGroupService

    fun getStaticProcessTemplateService(): StaticProcessTemplateService

    fun getProcessService(): CloudProcessService

    fun getCloudPlayerService(): CloudPlayerService

    fun getPermissionGroupService(): PermissionGroupService

    fun getNodeService(): NodeService

    fun getMessageChannelManager(): MessageChannelManager

    fun getEventManager(): EventManager

    fun getPermissionFactory(): Permission.Factory

    fun getCacheHandler(): CacheHandler

    fun isDisabledMode(): CompletableFuture<Boolean>

}