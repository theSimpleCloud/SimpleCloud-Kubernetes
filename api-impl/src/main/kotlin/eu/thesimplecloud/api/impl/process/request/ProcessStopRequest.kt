package eu.thesimplecloud.api.impl.process.request

import eu.thesimplecloud.api.internal.InternalCloudAPI
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.process.request.IProcessStopRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 09:38
 * @author Frederick Baier
 */
class ProcessStopRequest(
    private val process: ICloudProcess
) : IProcessStopRequest {

    override fun getProcess(): ICloudProcess {
        return this.process
    }

    override fun submit(): CompletableFuture<Void> {
        return InternalCloudAPI.instance.getProcessService().shutdownProcess(this.process)
    }
}