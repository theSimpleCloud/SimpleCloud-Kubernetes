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

import app.simplecloud.simplecloud.api.impl.request.template.group.update.CloudProxyGroupUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.request.group.update.CloudProxyGroupUpdateRequest
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.api.template.group.CloudProxyGroup

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 11:23
 * @author Frederick Baier
 */
class CloudProxyGroupImpl(
    private val configuration: ProxyProcessTemplateConfiguration,
    private val processGroupService: InternalCloudProcessGroupService,
) : AbstractCloudProcessGroup(
    configuration,
    processGroupService
), CloudProxyGroup {

    override fun getStartPort(): Int {
        return this.configuration.startPort
    }

    override fun createUpdateRequest(): CloudProxyGroupUpdateRequest {
        return CloudProxyGroupUpdateRequestImpl(this.processGroupService, this)
    }

}