package eu.thesimplecloud.api.service.onlinecount

import eu.thesimplecloud.api.process.onlineonfiguration.IProcessesOnlineCountConfiguration
import eu.thesimplecloud.api.service.IService
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 03.04.2021
 * Time: 22:22
 * @author Frederick Baier
 */
interface IProcessOnlineCountService : IService {

    fun findProcessOnlineCountConfigurationByName(name: String): CompletableFuture<IProcessesOnlineCountConfiguration>

}