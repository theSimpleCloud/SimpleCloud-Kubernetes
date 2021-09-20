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

package eu.thesimplecloud.simplecloud.container.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.Image
import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.container.BuiltImage
import eu.thesimplecloud.simplecloud.container.IImage
import eu.thesimplecloud.simplecloud.container.IImageRepository
import java.util.concurrent.CompletableFuture

@Singleton
class DockerImageRepository @Inject constructor(
    private val dockerClient: DockerClient,
) : IImageRepository {

    override fun getImageByName(name: String): CompletableFuture<IImage> {
        return CompletableFuture.supplyAsync {
            val image = findDockerImage(name)
            return@supplyAsync BuiltImage(name, image.id)
        }
    }

    private fun findDockerImage(name: String): Image {
        val images = this.dockerClient.listImagesCmd()
            .exec()
        return images.first { it.repoTags?.contains(name) ?: false }
    }

}