package eu.thesimplecloud.simplecloud.container

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
         * @param imageBuildInstructions the instructions to build the image from
         */
        fun create(
            name: String,
            buildDir: File,
            imageBuildInstructions: ImageBuildInstructions,
        ): IImage

    }

}