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

package app.simplecloud.simplecloud.node.api.processtemplate

import app.simplecloud.simplecloud.api.service.ProcessTemplateService
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.api.template.configuration.LobbyProcessTemplateConfiguration

/**
 * Date: 20.08.22
 * Time: 09:39
 * @author Frederick Baier
 *
 */
abstract class ProcessTemplateServiceBaseTest {

    protected lateinit var templateService: ProcessTemplateService<ProcessTemplate>

    open fun setUp() {
        this.templateService = getProcessTemplateService() as ProcessTemplateService<ProcessTemplate>
    }

    abstract fun getProcessTemplateService(): ProcessTemplateService<out ProcessTemplate>

    protected fun createLobbyTemplateConfiguration(name: String = "Lobby"): LobbyProcessTemplateConfiguration {
        return LobbyProcessTemplateConfiguration(
            name,
            512,
            20,
            false,
            "Test",
            false,
            0,
            null,
            0
        )
    }

}