package eu.thesimplecloud.api.service.process

import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.process.group.ICloudProcessGroup
import eu.thesimplecloud.api.service.IService
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 03.04.2021
 * Time: 19:27
 * @author Frederick Baier
 */
interface ICloudProcessService : IService {

    fun findProcessByName(name: String): CompletableFuture<ICloudProcess>

    fun findProcessesByName(vararg names: String): CompletableFuture<List<ICloudProcess>>

    fun findProcessesByGroup(group: ICloudProcessGroup): CompletableFuture<List<ICloudProcess>>

    fun findProcessesByGroup(groupName: String): CompletableFuture<List<ICloudProcess>>

}