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

package app.simplecloud.simplecloud.api.impl.player.factory

import app.simplecloud.simplecloud.api.impl.player.CloudPlayerImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionPlayer
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessService

/**
 * Date: 06.05.22
 * Time: 21:34
 * @author Frederick Baier
 *
 */
class CloudPlayerFactoryImpl(
    private val processService: CloudProcessService,
    private val permissionFactory: Permission.Factory,
    private val permissionPlayerFactory: PermissionPlayer.Factory
) : CloudPlayerFactory {

    override fun create(
        configuration: CloudPlayerConfiguration,
        cloudPlayerService: InternalCloudPlayerService
    ): CloudPlayer {
        return CloudPlayerImpl(
            configuration,
            cloudPlayerService,
            this.processService,
            this.permissionFactory,
            this.permissionPlayerFactory
        )
    }
}