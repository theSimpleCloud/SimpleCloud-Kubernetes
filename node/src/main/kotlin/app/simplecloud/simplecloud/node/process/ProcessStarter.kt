package app.simplecloud.simplecloud.node.process

import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess
import java.util.concurrent.CompletableFuture

interface ProcessStarter {

    fun startProcess(): CompletableFuture<CloudProcess>

    interface Factory {

        fun create(configuration: ProcessStartConfiguration): ProcessStarterImpl

    }

}