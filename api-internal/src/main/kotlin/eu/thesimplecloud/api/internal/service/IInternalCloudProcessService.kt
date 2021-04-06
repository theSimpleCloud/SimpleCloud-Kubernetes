package eu.thesimplecloud.api.internal.service

import eu.thesimplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.api.process.ICloudProcess
import eu.thesimplecloud.api.service.process.ICloudProcessService
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 19:58
 * @author Frederick Baier
 */
interface IInternalCloudProcessService : ICloudProcessService {

    /**
     * Starts a new process with the specified [configuration]
     * @return the newly registered process
     */
    fun startNewProcess(configuration: ProcessStartConfiguration): CompletableFuture<ICloudProcess>

}