package app.simplecloud.simplecloud.api.impl.request.process

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
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
    private var onlinePlayers: Int = this.process.getOnlinePlayers()
    @Volatile
    private var processState: ProcessState = this.process.getState()
    @Volatile
    private var visible: Boolean = this.process.isVisible()

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

    override fun setOnlinePlayers(onlinePlayers: Int): InternalProcessUpdateRequest {
        this.onlinePlayers = onlinePlayers
        return this
    }

    override fun setVisible(visible: Boolean): InternalProcessUpdateRequest {
        this.visible = visible
        return this
    }

    override fun submit(): CompletableFuture<Unit> = CloudScope.future {
        val configuration = CloudProcessConfiguration(
            process.getGroupName(),
            process.getUniqueId(),
            process.getProcessNumber(),
            processState,
            visible,
            process.getMaxMemory(),
            process.getUsedMemory(),
            maxPlayers,
            onlinePlayers,
            process.isStatic(),
            process.getProcessType(),
            process.getImage().getName(),
            igniteId
        )
        internalService.updateProcessInternal(configuration)
    }

    private fun getIgniteIdIfSet(): UUID? {
        return runCatching { this.process.getIgniteId() }.getOrNull()
    }

}