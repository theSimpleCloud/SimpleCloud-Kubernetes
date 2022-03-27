package app.simplecloud.simplecloud.node.process

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.state.ProcessState
import app.simplecloud.simplecloud.kubernetes.api.container.Container
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import org.apache.logging.log4j.LogManager

/**
 * Date: 27.03.22
 * Time: 18:49
 * @author Frederick Baier
 *
 */
class ProcessShutdownHandlerImpl @Inject constructor(
    @Assisted private val process: CloudProcess,
    private val containerFactory: Container.Factory,
    private val igniteCloudProcessRepository: IgniteCloudProcessRepository
) : ProcessShutdownHandler {

    override suspend fun shutdownProcess() {
        logger.info("Stopping Process {}", this.process.getName())
        updateStateToClosed()
        val container = this.containerFactory.create(this.process.getName().lowercase())
        container.shutdown()
        deleteProcessInCluster()
    }

    private suspend fun updateStateToClosed() {
        val updateRequest = this.process.createUpdateRequest()
        updateRequest as InternalProcessUpdateRequest
        updateRequest.setState(ProcessState.CLOSED)
        updateRequest.submit().await()
    }

    private fun deleteProcessInCluster() {
        this.igniteCloudProcessRepository.remove(this.process.getName())
    }

    companion object {
        private val logger = LogManager.getLogger(ProcessShutdownHandlerImpl::class.java)
    }

}