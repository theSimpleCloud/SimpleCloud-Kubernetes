package eu.thesimplecloud.api.repository.process

import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.repository.IRepository
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 03.04.2021
 * Time: 19:26
 * @author Frederick Baier
 */
interface ICloudProcessRepository : IRepository<String, ICloudProcess> {

    /**
     * Returns the process found by the specified [uniqueId]
     */
    fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<ICloudProcess>

}