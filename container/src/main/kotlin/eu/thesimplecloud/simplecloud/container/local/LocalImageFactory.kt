package eu.thesimplecloud.simplecloud.container.local

import eu.thesimplecloud.simplecloud.container.IImage
import eu.thesimplecloud.simplecloud.container.IImageInclusion
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 21:39
 * @author Frederick Baier
 */
class LocalImageFactory : IImage.Factory {

    override fun create(name: String, directories: List<File>, inclusions: List<IImageInclusion>): LocalImage {
        return LocalImage(name, directories, inclusions)
    }
}