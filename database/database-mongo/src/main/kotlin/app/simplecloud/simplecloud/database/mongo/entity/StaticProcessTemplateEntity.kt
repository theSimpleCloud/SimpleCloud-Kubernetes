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

package app.simplecloud.simplecloud.database.mongo.entity

import app.simplecloud.simplecloud.api.template.ProcessTemplateType
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ServerProcessTemplateConfiguration
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id

@Entity("static_templates")
class StaticProcessTemplateEntity(
    @Id
    val name: String,
    val maxMemory: Int,
    val maxPlayers: Int,
    val maintenance: Boolean,
    val imageName: String?,
    val stateUpdating: Boolean,
    val startPriority: Int,
    val joinPermission: String?,
    val type: ProcessTemplateType,
    val lobbyPriority: Int = -1,
    val startPort: Int = -1,
) {

    private constructor() : this(
        "<>",
        1,
        1,
        false,
        "",
        false,
        1,
        "",
        ProcessTemplateType.PROXY
    )

    fun toConfiguration(): AbstractProcessTemplateConfiguration {
        when (type) {
            ProcessTemplateType.LOBBY -> {
                return LobbyProcessTemplateConfiguration(
                    this.name,
                    this.maxMemory,
                    this.maxPlayers,
                    this.maintenance,
                    this.imageName,
                    this.stateUpdating,
                    this.startPriority,
                    this.joinPermission,
                    this.lobbyPriority
                )
            }

            ProcessTemplateType.PROXY -> {
                return ProxyProcessTemplateConfiguration(
                    this.name,
                    this.maxMemory,
                    this.maxPlayers,
                    this.maintenance,
                    this.imageName,
                    this.stateUpdating,
                    this.startPriority,
                    this.joinPermission,
                    this.startPort
                )
            }

            ProcessTemplateType.SERVER -> {
                return ServerProcessTemplateConfiguration(
                    this.name,
                    this.maxMemory,
                    this.maxPlayers,
                    this.maintenance,
                    this.imageName,
                    this.stateUpdating,
                    this.startPriority,
                    this.joinPermission,
                )
            }
        }
    }

    companion object {
        fun fromGroupConfiguration(configuration: AbstractProcessTemplateConfiguration): StaticProcessTemplateEntity {
            val startPort = if (configuration is ProxyProcessTemplateConfiguration) configuration.startPort else -1
            val lobbyPriority =
                if (configuration is LobbyProcessTemplateConfiguration) configuration.lobbyPriority else -1
            return StaticProcessTemplateEntity(
                configuration.name,
                configuration.maxMemory,
                configuration.maxPlayers,
                configuration.maintenance,
                configuration.imageName,
                configuration.stateUpdating,
                configuration.startPriority,
                configuration.joinPermission,
                configuration.type,
                lobbyPriority,
                startPort
            )
        }
    }

}