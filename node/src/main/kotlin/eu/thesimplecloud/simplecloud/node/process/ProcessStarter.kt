package eu.thesimplecloud.simplecloud.node.process

import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.process.CloudProcess
import java.util.concurrent.CompletableFuture

interface ProcessStarter {

    fun startProcess(): CompletableFuture<CloudProcess>

    interface Factory {

        fun create(configuration: ProcessStartConfiguration): ProcessStarterImpl

    }

}