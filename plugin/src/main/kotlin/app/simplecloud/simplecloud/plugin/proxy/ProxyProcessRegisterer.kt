package app.simplecloud.simplecloud.plugin.proxy

import app.simplecloud.simplecloud.api.impl.repository.ignite.IgniteCloudProcessRepository
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessService
import com.google.inject.Inject
import org.apache.ignite.Ignite
import org.apache.ignite.events.EventType

/**
 * Date: 24.01.22
 * Time: 19:26
 * @author Frederick Baier
 *
 */
class ProxyProcessRegisterer @Inject constructor(
    private val cloudProcessService: CloudProcessService,
    private val proxyServerRegistry: ProxyServerRegistry,
    private val processRepository: IgniteCloudProcessRepository,
    //private val igniteUpdateListener: ProxyIgniteUpdateListener,
    private val ignite: Ignite
) {

    fun registerExistingProcesses() {
        val completableFuture = this.cloudProcessService.findAll()
        completableFuture.thenAccept { list-> list.forEach { registerProcess(it) } }
    }

    fun registerIgniteListener() {
        println("Local listen started")
        this.ignite.events().localListen({ event ->
            println("local listen: " + event.message())
            return@localListen false
        }, EventType.EVT_CACHE_OBJECT_PUT)
    }

    private fun registerProcess(cloudProcess: CloudProcess) {
        println("Proxy found process ${cloudProcess.getName()}")
        this.proxyServerRegistry.registerProcess(cloudProcess)
    }



}