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

package app.simplecloud.simplecloud.api.impl.template.group

import app.simplecloud.simplecloud.api.impl.template.AbstractProcessTemplate
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupDeleteRequest
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 12:01
 * @author Frederick Baier
 *
 * Represents a process group exposed by the api to allow easier accessibility of associated objects
 */
abstract class AbstractCloudProcessGroup constructor(
    private val configuration: AbstractProcessTemplateConfiguration,
    private val processGroupService: InternalCloudProcessGroupService,
) : AbstractProcessTemplate(configuration), CloudProcessGroup {

    override fun getOnlinePlayerCount(): Int {
        TODO()
    }

    override fun isStatic(): Boolean {
        return false
    }

    override fun createDeleteRequest(): CloudProcessGroupDeleteRequest {
        return this.processGroupService.createDeleteRequest(this)
    }

}