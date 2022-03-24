package app.simplecloud.simplecloud.plugin.startup

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessService
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.*

@Singleton
class SelfProcessGetter @Inject constructor(
    private val processService: CloudProcessService
) {

    private val internalProcessId = UUID.fromString(System.getenv("SIMPLECLOUD_PROCESS_ID"))

    private val cloudProcess = this.processService.findByUniqueId(this.internalProcessId).join()

    fun getSelfProcess(): CloudProcess {
        return this.cloudProcess
    }

}