package eu.thesimplecloud.simplecloud.container

import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 10.04.2021
 * Time: 21:27
 * @author Frederick Baier
 *
 * Represents a file to include into an image
 *
 */
interface IImageInclusion {

    /**
     * Returns the file to include
     * This method may be blocking to download a file for example
     */
    fun getFile(): File

    /**
     * Returns the path of the inclusion when in included in an image
     */
    fun getPathInImage(): String

}