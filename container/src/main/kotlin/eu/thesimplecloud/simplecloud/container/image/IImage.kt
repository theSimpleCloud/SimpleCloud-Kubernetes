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

package eu.thesimplecloud.simplecloud.container.image

import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 20:52
 * @author Frederick Baier
 */
interface IImage {

    /**
     * Returns the name of this image
     */
    fun getName(): String

    /**
     * Returns whether this image was built
     */
    fun isBuilt(): Boolean

    /**
     * Builds this image if the image has not been built yet
     * @return an identifier for the built image
     */
    fun build(): CompletableFuture<String>

    /**
     * The factory to build images
     */
    interface Factory {

        /**
         * Creates an image
         * @param name the name of the image
         * @param persistentVolumePath the path to the dir that gets mounted into the build container
         * @param imageBuildInstructions the instructions to build the image from
         */
        fun create(
            name: String,
            persistentVolumePath: String,
            imageBuildInstructions: ImageBuildInstructions,
        ): IImage

    }

}