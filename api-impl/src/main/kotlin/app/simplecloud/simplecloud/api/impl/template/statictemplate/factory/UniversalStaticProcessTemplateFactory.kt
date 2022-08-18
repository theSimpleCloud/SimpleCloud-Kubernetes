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

package app.simplecloud.simplecloud.api.impl.template.statictemplate.factory

import app.simplecloud.simplecloud.api.internal.service.InternalStaticProcessTemplateService
import app.simplecloud.simplecloud.api.template.ProcessTemplateType
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ServerProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.static.StaticProcessTemplate

/**
 * Created by IntelliJ IDEA.
 * Date: 02/07/2021
 * Time: 11:03
 * @author Frederick Baier
 */
class UniversalStaticProcessTemplateFactory(
    private val staticLobbyTemplateFactory: StaticLobbyTemplateFactory,
    private val staticProxyTemplateFactory: StaticProxyTemplateFactory,
    private val staticServerTemplateFactory: StaticServerTemplateFactory,
) {

    fun create(
        configuration: AbstractProcessTemplateConfiguration,
        internalService: InternalStaticProcessTemplateService,
    ): StaticProcessTemplate {
        return when (configuration.type) {
            ProcessTemplateType.PROXY -> {
                this.staticProxyTemplateFactory.create(
                    configuration as ProxyProcessTemplateConfiguration,
                    internalService
                )
            }

            ProcessTemplateType.LOBBY -> {
                this.staticLobbyTemplateFactory.create(
                    configuration as LobbyProcessTemplateConfiguration,
                    internalService
                )
            }

            ProcessTemplateType.SERVER -> {
                this.staticServerTemplateFactory.create(
                    configuration as ServerProcessTemplateConfiguration,
                    internalService
                )
            }
        }
    }

}