package eu.thesimplecloud.simplecloud.container

import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 18.04.2021
 * Time: 10:28
 * @author Frederick Baier
 */
class FileImageInclusion(
    private val file: File,
    private val pathInImage: String
) : IImageInclusion {

    override fun getFile(): File {
        return this.file
    }

    override fun getPathInImage(): String {
        return this.pathInImage
    }
}