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

package app.simplecloud.simplecloud.node.resource.process.execute

import app.simplecloud.simplecloud.api.internal.configutation.ProcessExecuteCommandConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePodService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceCustomActionHandler
import app.simplecloud.simplecloud.node.process.InternalProcessCommandExecutor

/**
 * Date: 18.03.23
 * Time: 10:19
 * @author Frederick Baier
 *
 */
class V1Beta1CloudProcessExecuteHandler(
    private val processService: CloudProcessService,
    private val podService: KubePodService,
) : ResourceCustomActionHandler<V1Beta1CloudProcessExecuteBody> {

    override fun handleAction(resourceName: String, body: V1Beta1CloudProcessExecuteBody) {
        checkProcessExists(resourceName)
        println("Calling command execute with ${resourceName} : ${body.command}")
        InternalProcessCommandExecutor(
            ProcessExecuteCommandConfiguration(resourceName.lowercase(), body.command),
            this.podService
        ).executeCommand()
    }

    private fun checkProcessExists(resourceName: String) {
        this.processService.findByName(resourceName).join()
    }

}