package eu.thesimplecloud.simplecloud.container.local

import eu.thesimplecloud.simplecloud.container.IContainer
import eu.thesimplecloud.simplecloud.container.IImage
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 21:17
 * @author Frederick Baier
 */
class LocalContainer(
    private val name: String,
    private val image: LocalImage,
    private val startCommand: String,
    private val stopCommand: String,
) : IContainer {

    private val workingDir = File("$CONTAINERS_DIR$name/")

    private val executor = LocalContainerExecutor(startCommand, stopCommand, image, workingDir)

    override fun getName(): String {
        return this.name
    }

    override fun getImage(): IImage {
        return this.image
    }

    override fun execute(command: String) {
        this.executor.executeCommand(command)
    }

    override fun start() {
        this.executor.startContainer()
    }

    override fun shutdown(): CompletableFuture<Void> {
        return this.executor.shutdownContainer()
    }

    override fun terminationFuture(): CompletableFuture<Void> {
        return this.executor.terminationFuture()
    }

    override fun forceShutdown() {
        this.executor.forceShutdown()
    }

    override fun isRunning(): Boolean {
        return this.executor.isContainerRunning()
    }

    override fun getLogs(): List<String> {
        return this.executor.getLogs()
    }

    companion object {
        const val CONTAINERS_DIR = "containers/"
    }

}