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
     */
    fun build(): CompletableFuture<Void>

    /**
     * The factory to build images
     */
    interface Factory {

        /**
         * Creates an image
         * @param name the name of the image
         * @param directories the directories to create the image from
         */
        fun create(
            name: String,
            directories: List<File>,
            inclusions: List<IImageInclusion> = emptyList()
        ): IImage

    }

}