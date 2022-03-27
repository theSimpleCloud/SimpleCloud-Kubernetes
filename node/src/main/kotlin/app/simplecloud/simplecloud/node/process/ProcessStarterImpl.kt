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

package app.simplecloud.simplecloud.node.process

import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.util.ClusterKey
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.kubernetes.api.container.Container
import app.simplecloud.simplecloud.kubernetes.api.service.KubeService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import app.simplecloud.simplecloud.node.task.CloudProcessCreator
import app.simplecloud.simplecloud.node.task.KubernetesProcessStarter
import app.simplecloud.simplecloud.node.util.UncaughtExceptions
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted

class ProcessStarterImpl @Inject constructor(
    @Assisted private val configuration: ProcessStartConfiguration,
    private val processService: CloudProcessService,
    private val groupService: CloudProcessGroupService,
    private val processFactory: CloudProcessFactory,
    private val containerFactory: Container.Factory,
    private val kubeVolumeClaimFactory: KubeVolumeClaim.Factory,
    private val kubeServiceFactory: KubeService.Factory,
    private val clusterKey: ClusterKey
) : ProcessStarter {

    override suspend fun startProcess(): CloudProcess {
        val process = createProcess(this.configuration)
        startProcess(process)
        return process
    }

    private suspend fun createProcess(configuration: ProcessStartConfiguration): CloudProcess {
        return CloudProcessCreator(
            configuration,
            this.processService,
            this.groupService,
            this.processFactory
        ).createProcess()
    }

    private fun startProcess(process: CloudProcess) {
        KubernetesProcessStarter(
            process,
            this.containerFactory,
            this.kubeVolumeClaimFactory,
            this.kubeServiceFactory,
            this.clusterKey
        ).startProcess().exceptionally { UncaughtExceptions.handle(it) }
    }


}