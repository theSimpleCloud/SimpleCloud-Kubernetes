package eu.thesimplecloud.api.service.processversion

import eu.thesimplecloud.api.process.version.IProcessVersion
import eu.thesimplecloud.api.service.IService
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 02.04.2021
 * Time: 19:32
 * @author Frederick Baier
 */
interface IProcessVersionService : IService {

    fun findByName(name: String): CompletableFuture<IProcessVersion>

}