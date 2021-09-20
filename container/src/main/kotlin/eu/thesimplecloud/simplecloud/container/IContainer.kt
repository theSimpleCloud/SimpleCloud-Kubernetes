package eu.thesimplecloud.simplecloud.container

import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 19:11
 * @author Frederick Baier
 */
interface IContainer {

    /**
     * Returns the name of this container
     */
    fun getName(): String

    /**
     * Returns the image this container is using
     */
    fun getImage(): IImage

    /**
     * Executes the specified [command]
     */
    fun execute(command: String)

    /**
     * Starts this container
     */
    fun start()

    /**
     * Shuts this container down
     * @return [terminationFuture]
     */
    fun shutdown(): CompletableFuture<Unit>

    /**
     * Returns a future that completes when the container was terminated
     */
    fun terminationFuture(): CompletableFuture<Unit>

    /**
     * Shuts this container down immediately
     */
    fun forceShutdown()

    /**
     * Returns whether this container is running
     */
    fun isRunning(): Boolean

    /**
     * Returns the logs saved
     */
    fun getLogs(): List<String>

    /**
     * Copies a file from this container to the specified [dest]
     */
    fun copyFromContainer(source: String, dest: File)

    /**
     * Deletes the container once it stops or dies
     */
    fun deleteOnShutdown()


    interface Factory {

        /**
         * Creates a container
         */
        fun create(
            name: String,
            image: IImage,
            containerSpec: ContainerSpec
        ): IContainer

    }

}