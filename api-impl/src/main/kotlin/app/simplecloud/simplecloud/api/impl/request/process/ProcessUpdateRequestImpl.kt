package app.simplecloud.simplecloud.api.impl.request.process

import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessService
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.process.state.ProcessState
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 05.01.22
 * Time: 17:36
 * @author Frederick Baier
 *
 */
class ProcessUpdateRequestImpl(
    private val internalService: InternalCloudProcessService,
    private val process: CloudProcess
) : InternalProcessUpdateRequest {

    @Volatile
    private var igniteId: UUID? = getIgniteIdIfSet()
    @Volatile
    private var maxPlayers: Int = this.process.getMaxPlayers()
    @Volatile
    private var processState: ProcessState = this.process.getState()

    override fun getProcess(): CloudProcess {
        return this.process
    }

    override fun setIgniteId(id: UUID): InternalProcessUpdateRequest {
        this.igniteId = id
        return this
    }

    override fun setState(processState: ProcessState): InternalProcessUpdateRequest {
        this.processState = processState
        return this
    }

    override fun setMaxPlayers(maxPlayers: Int): InternalProcessUpdateRequest {
        this.maxPlayers = maxPlayers
        return this
    }

    override fun submit(): CompletableFuture<Unit> {
        val configuration = CloudProcessConfiguration(
            this.process.getGroupName(),
            this.process.getUniqueId(),
            this.process.getProcessNumber(),
            this.processState,
            this.process.getMaxMemory(),
            this.process.getUsedMemory(),
            this.maxPlayers,
            this.process.isStatic(),
            this.process.getProcessType(),
            this.process.getImage().getName(),
            this.igniteId
        )
        return this.internalService.updateProcessInternal(configuration)
    }

    private fun getIgniteIdIfSet(): UUID? {
        return runCatching { this.process.getIgniteId() }.getOrNull()
    }

}