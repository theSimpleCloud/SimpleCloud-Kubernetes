package eu.thesimplecloud.simplecloud.node.process

import eu.thesimplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import java.util.concurrent.CompletableFuture

class InternalProcessStartHandler(
    private val processStarterFactory: ProcessStarter.Factory,
    private val igniteRepository: IgniteCloudProcessRepository,
    private val configuration: ProcessStartConfiguration
) {

    private val processStarter = this.processStarterFactory.create(this.configuration)

    fun startProcess(): CompletableFuture<CloudProcess> {
        val future = this.processStarter.startProcess()
        updateProcessToCluster(future)
        return future
    }

    private fun updateProcessToCluster(future: CompletableFuture<CloudProcess>): CompletableFuture<Unit> {
        return future.thenApply { updateProcessToCluster(it) }
    }

    private fun updateProcessToCluster(process: CloudProcess): CompletableFuture<Unit> {
        return this.igniteRepository.save(process.getName(), process.toConfiguration())
    }

}