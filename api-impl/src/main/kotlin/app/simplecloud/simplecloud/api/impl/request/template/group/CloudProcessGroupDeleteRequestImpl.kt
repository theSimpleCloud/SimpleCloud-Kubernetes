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

package app.simplecloud.simplecloud.api.impl.request.template.group

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.request.group.CloudProcessGroupDeleteRequest
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 21:43
 * @author Frederick Baier
 */
class CloudProcessGroupDeleteRequestImpl(
    private val internalService: InternalCloudProcessGroupService,
    private val processGroup: CloudProcessGroup
) : CloudProcessGroupDeleteRequest {

    override fun getProcessTemplate(): CloudProcessGroup {
        return this.processGroup
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        internalService.deleteGroupInternal(processGroup)
    }
}