package app.simplecloud.simplecloud.plugin.startup

import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.process.state.ProcessState
import com.google.inject.Inject
import org.apache.ignite.Ignite

class SelfIgniteProcessUpdater @Inject constructor(
    private val ignite: Ignite,
    private val selfProcessGetter: SelfProcessGetter
) {

    private val igniteSelfId = this.ignite.cluster().localNode().id()

    private val cloudProcess = this.selfProcessGetter.getSelfProcess()

    fun updateProcessInIgniteBlocking() {
        val updateRequest = cloudProcess.createUpdateRequest()
        updateRequest as InternalProcessUpdateRequest
        updateRequest.setIgniteId(this.igniteSelfId)
        updateRequest.setState(ProcessState.ONLINE)
        updateRequest.submit().join()
    }

}