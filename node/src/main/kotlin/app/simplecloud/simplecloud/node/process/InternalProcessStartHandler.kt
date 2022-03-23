package app.simplecloud.simplecloud.node.process

import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessService
import java.util.concurrent.CompletableFuture

class InternalProcessStartHandler(
    private val processStarterFactory: ProcessStarter.Factory,
    private val processService: CloudProcessService,
    private val configuration: ProcessStartConfiguration
) {

    private val processStarter = this.processStarterFactory.create(this.configuration)

    suspend fun startProcess(): CloudProcess {
        val process = this.processStarter.startProcess()
        updateProcessToCluster(process)
        return process
    }

    private fun updateProcessToCluster(process: CloudProcess): CompletableFuture<Unit> {
        return this.processService.createUpdateRequest(process).submit()
    }

}