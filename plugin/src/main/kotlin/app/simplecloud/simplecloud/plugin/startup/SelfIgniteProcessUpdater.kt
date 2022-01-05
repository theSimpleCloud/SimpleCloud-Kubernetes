package app.simplecloud.simplecloud.plugin.startup

import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.service.CloudProcessService
import com.google.inject.Inject
import org.apache.ignite.Ignite
import java.util.*

class SelfIgniteProcessUpdater @Inject constructor(
    private val processService: CloudProcessService,
    private val ignite: Ignite
) {

    private val internalProcessId = UUID.fromString(System.getenv("SIMPLECLOUD_PROCESS_ID"))

    private val igniteSelfId = this.ignite.cluster().localNode().id()

    fun updateProcessInIgniteBlocking() {
        val cloudProcess = this.processService.findProcessByUniqueId(this.internalProcessId).join()
        val updateRequest = cloudProcess.createUpdateRequest()
        updateRequest as InternalProcessUpdateRequest
        updateRequest.setIgniteId(this.igniteSelfId)
        updateRequest.submit().join()
    }

}