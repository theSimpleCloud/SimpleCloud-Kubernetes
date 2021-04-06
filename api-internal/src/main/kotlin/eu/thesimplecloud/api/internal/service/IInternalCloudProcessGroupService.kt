package eu.thesimplecloud.api.internal.service

import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.service.group.ICloudProcessGroupService
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 18:02
 * @author Frederick Baier
 */
interface IInternalCloudProcessGroupService : ICloudProcessGroupService {

    fun updateGroup(group: ICloudProcessGroup): CompletableFuture<ICloudProcessGroup>

    fun deleteGroup(group: ICloudProcessGroup): CompletableFuture<Void>

}