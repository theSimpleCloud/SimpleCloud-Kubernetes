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

package app.simplecloud.simplecloud.node.resource.cluster.update

import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.api.internal.service.InternalCloudStateService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceCustomActionHandler
import app.simplecloud.simplecloud.module.api.service.ErrorService
import app.simplecloud.simplecloud.node.update.NodeUpdater
import kotlin.concurrent.thread

/**
 * Date: 05.04.23
 * Time: 11:46
 * @author Frederick Baier
 *
 */
class V1Beta1ClusterUpdateHandler(
    private val environmentVariables: EnvironmentVariables,
    private val stateService: InternalCloudStateService,
    private val ftpServerService: InternalFtpServerService,
    private val processService: CloudProcessService,
    private val errorService: ErrorService,
    private val kubeAPI: KubeAPI,
) : ResourceCustomActionHandler<V1Beta1ClusterUpdateBody> {

    override fun handleAction(resourceName: String, body: V1Beta1ClusterUpdateBody) {
        val buildKitAddr = environmentVariables.get("BUILDKIT_ADDR")
            ?: throw NoSuchElementException("Environment variable BUILDKIT_ADDR is not set")
        val registryAddr = environmentVariables.get("REBUILD_REGISTRY")
            ?: throw NoSuchElementException("Environment variable REBUILD_REGISTRY is not set")
        val nodeUpdater = NodeUpdater(
            body.moduleLinks.toList(),
            body.baseImage,
            buildKitAddr,
            "${registryAddr}/simplecloud-internal:latest",
            this.stateService,
            this.ftpServerService,
            this.processService,
            this.errorService,
            this.kubeAPI
        )
        if (!nodeUpdater.canPerformUpdate()) {
            throw UnableToUpdateException()
        }
        thread {
            try {
                nodeUpdater.executeUpdate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    class UnableToUpdateException() : Exception("Unable to update")

}