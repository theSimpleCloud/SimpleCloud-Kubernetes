package eu.thesimplecloud.api.service.group

import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.service.IService
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 02.04.2021
 * Time: 12:28
 * @author Frederick Baier
 */
interface ICloudProcessGroupService : IService {

    fun findByUniqueId(uniqueId: UUID): CompletableFuture<ICloudProcessGroup>

    fun findByName(name: String): CompletableFuture<ICloudProcessGroup>

}