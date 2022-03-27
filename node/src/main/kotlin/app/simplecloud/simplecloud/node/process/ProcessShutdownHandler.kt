package app.simplecloud.simplecloud.node.process

import app.simplecloud.simplecloud.api.process.CloudProcess

interface ProcessShutdownHandler {

    suspend fun shutdownProcess()

    interface Factory {

        fun create(process: CloudProcess): ProcessShutdownHandler

    }

}