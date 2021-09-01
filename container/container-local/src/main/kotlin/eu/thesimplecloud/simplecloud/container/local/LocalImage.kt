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

    override fun build(): CompletableFuture<String> {
        return this.localImageBuilder.build()
    }

    fun copyBuildImageTo(dir: File) {
        FileUtils.copyDirectory(imageDir, dir)
    }

    companion object {
        const val IMAGES_DIR = "images/"
    }

}