/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.kubernetes.container

import eu.thesimplecloud.simplecloud.api.image.Image
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 19:11
 * @author Frederick Baier
 */
interface Container {

    /**
     * Returns the name of this container
     */
    fun getName(): String

    /**
     * Returns the image this container is using
     */
    fun getImage(): Image

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
            image: Image,
            containerSpec: ContainerSpec
        ): Container

    }

}