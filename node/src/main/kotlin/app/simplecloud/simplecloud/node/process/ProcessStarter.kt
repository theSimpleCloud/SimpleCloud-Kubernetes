package app.simplecloud.simplecloud.node.process

import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.process.CloudProcess

interface ProcessStarter {

    suspend fun startProcess(): CloudProcess

    interface Factory {

        fun create(configuration: ProcessStartConfiguration): ProcessStarter

    }

}