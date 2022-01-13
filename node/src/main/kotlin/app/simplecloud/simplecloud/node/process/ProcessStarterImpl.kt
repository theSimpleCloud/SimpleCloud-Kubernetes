/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package app.simplecloud.simplecloud.node.process

import app.simplecloud.simplecloud.api.future.completedFuture
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.util.ClusterKey
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.kubernetes.api.container.Container
import app.simplecloud.simplecloud.kubernetes.api.service.KubeService
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import app.simplecloud.simplecloud.node.task.CloudProcessCreationTask
import app.simplecloud.simplecloud.node.task.ProcessStartTask
import app.simplecloud.simplecloud.node.util.UncaughtExceptions
import com.ea.async.Async.await
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import java.util.concurrent.CompletableFuture

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

    override fun startProcess(): CompletableFuture<CloudProcess> {
        val process = await(createProcess(configuration))
        startProcess(process)
        return completedFuture(process)
    }

    private fun createProcess(configuration: ProcessStartConfiguration): CompletableFuture<CloudProcess> {
        return CloudProcessCreationTask(
            configuration,
            this.processService,
            this.groupService,
            this.processFactory
        ).run()
    }

    private fun startProcess(process: CloudProcess) {
        ProcessStartTask(
            process,
            this.containerFactory,
            this.kubeVolumeClaimFactory,
            this.kubeServiceFactory,
            this.clusterKey
        ).run().exceptionally { UncaughtExceptions.handle(it) }
    }


}