package eu.thesimplecloud.simplecloud.container.local

import eu.thesimplecloud.simplecloud.container.IImageInclusion
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 09.04.2021
 * Time: 11:34
 * @author Frederick Baier
 */
class LocalImageBuilder(
    private val imageDir: File,
    private val directories: List<File>,
    private val inclusions: List<IImageInclusion>
) {

    @Volatile
    private var buildFuture: CompletableFuture<Void>? = null

    fun isBuilt(): Boolean {
        return this.buildFuture?.isDone ?: false
    }

    fun build(): CompletableFuture<Void> {
        return if (hasBuildStarted()) {
            this.buildFuture!!
        } else {
            this.startBuild()
        }
    }

    private fun hasBuildStarted(): Boolean {
        return this.buildFuture != null
    }

    private fun startBuild(): CompletableFuture<Void> {
        val buildFuture = runBuildInCompletableFuture()
        this.buildFuture = buildFuture
        return buildFuture
    }

    private fun runBuildInCompletableFuture(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            executeBuild()
        }
    }

    private fun executeBuild() {
        copyDirectoriesIntoImageDirectory()
        includeInclusions()
    }

    private fun includeInclusions() {
        this.inclusions.forEach {
            includeInclusion(it)
        }
    }

    private fun includeInclusion(inclusion: IImageInclusion) {
        val fileToInclude = inclusion.getFile()
        val pathInImage = inclusion.getPathInImage()
        val destFile = File(this.imageDir, pathInImage)
        FileUtils.copyFile(fileToInclude, destFile)
    }

    private fun copyDirectoriesIntoImageDirectory() {
        this.directories.forEach {
            FileUtils.copyDirectory(it, this.imageDir)
        }
    }

}