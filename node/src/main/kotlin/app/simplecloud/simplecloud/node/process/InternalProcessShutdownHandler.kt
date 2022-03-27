package app.simplecloud.simplecloud.node.process

import app.simplecloud.simplecloud.api.process.CloudProcess

/**
 * Date: 27.03.22
 * Time: 18:31
 * @author Frederick Baier
 *
 */
class InternalProcessShutdownHandler(
    private val process: CloudProcess,
    private val processShutdownHandlerFactory: ProcessShutdownHandler.Factory
) {

    private val processShutdownHandler = this.processShutdownHandlerFactory.create(this.process)

    suspend fun shutdownProcess() {
        this.processShutdownHandler.shutdownProcess()
    }

}