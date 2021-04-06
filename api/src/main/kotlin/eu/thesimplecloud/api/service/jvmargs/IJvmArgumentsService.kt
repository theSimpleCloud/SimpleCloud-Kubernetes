package eu.thesimplecloud.api.service.jvmargs

import eu.thesimplecloud.api.jvmargs.IJVMArguments
import eu.thesimplecloud.api.service.IService
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 02.04.2021
 * Time: 19:42
 * @author Frederick Baier
 */
interface IJvmArgumentsService : IService {

    fun findByName(name: String): CompletableFuture<IJVMArguments>

}