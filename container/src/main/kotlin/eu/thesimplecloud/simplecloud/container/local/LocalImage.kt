package eu.thesimplecloud.simplecloud.container.local

import eu.thesimplecloud.simplecloud.container.IImage
import eu.thesimplecloud.simplecloud.container.IImageInclusion
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07.04.2021
 * Time: 21:30
 * @author Frederick Baier
 */
class LocalImage(
    private val name: String,
    private val directories: List<File>,
    private val inclusions: List<IImageInclusion>
) : IImage {

    val imageDir = File("$IMAGES_DIR$name/")

    private val localImageBuilder = LocalImageBuilder(imageDir, directories, inclusions)

    override fun getName(): String {
        return this.name
    }

    override fun isBuilt(): Boolean {
        return this.localImageBuilder.isBuilt()
    }

    override fun build(): CompletableFuture<Void> {
        return this.localImageBuilder.build()
    }

    fun copyBuildImageTo(dir: File) {
        FileUtils.copyDirectory(imageDir, dir)
    }

    companion object {
        const val IMAGES_DIR = "images/"
    }

}