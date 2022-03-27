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

package app.simplecloud.simplecloud.api.impl.process.group.factory

import app.simplecloud.simplecloud.api.process.group.*
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration
import app.simplecloud.simplecloud.api.validator.GroupConfigurationValidator
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * Created by IntelliJ IDEA.
 * Date: 02/07/2021
 * Time: 11:03
 * @author Frederick Baier
 */
@Singleton
class CloudProcessGroupFactory @Inject constructor(
    private val lobbyGroupFactory: CloudLobbyGroup.Factory,
    private val proxyGroupFactory: CloudProxyGroup.Factory,
    private val serverGroupFactory: CloudServerGroup.Factory,
    private val groupConfigurationValidator: GroupConfigurationValidator
) {

    fun create(configuration: AbstractCloudProcessGroupConfiguration): CloudProcessGroup {
        this.groupConfigurationValidator.validate(configuration)

        return when (configuration.type) {
            ProcessGroupType.PROXY -> {
                this.proxyGroupFactory.create(configuration as CloudProxyProcessGroupConfiguration)
            }
            ProcessGroupType.LOBBY -> {
                this.lobbyGroupFactory.create(configuration as CloudLobbyProcessGroupConfiguration)
            }
            ProcessGroupType.SERVER -> {
                this.serverGroupFactory.create(configuration as CloudServerProcessGroupConfiguration)
            }
        }
    }

}