package eu.thesimplecloud.api.impl.process.request

import eu.thesimplecloud.api.internal.InternalCloudAPI
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.process.request.IProcessGroupDeleteRequest
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 21:43
 * @author Frederick Baier
 */
class ProcessGroupDeleteRequest(
    private val processGroup: ICloudProcessGroup
) : IProcessGroupDeleteRequest {
    override fun getProcessGroup(): ICloudProcessGroup {
        return this.processGroup
    }

    override fun submit(): CompletableFuture<Void> {
        return InternalCloudAPI.instance.getProcessGroupService().deleteGroup(this.processGroup)
    }
}