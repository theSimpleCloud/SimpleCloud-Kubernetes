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

package eu.thesimplecloud.simplecloud.container.docker.guice

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import eu.thesimplecloud.simplecloud.container.IContainer
import eu.thesimplecloud.simplecloud.container.IImage
import eu.thesimplecloud.simplecloud.container.IImageRepository
import eu.thesimplecloud.simplecloud.container.docker.DockerContainer
import eu.thesimplecloud.simplecloud.container.docker.DockerImage
import eu.thesimplecloud.simplecloud.container.docker.DockerImageRepository

/**
 * Created by IntelliJ IDEA.
 * Date: 09/08/2021
 * Time: 09:32
 * @author Frederick Baier
 */
class BinderModule : AbstractModule() {

    override fun configure() {
        val dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build()
        val dockerClient = DockerClientBuilder.getInstance(dockerConfig).build()
        bind(DockerClient::class.java).toInstance(dockerClient)
        bind(IImageRepository::class.java).to(DockerImageRepository::class.java)

        install(
            FactoryModuleBuilder()
                .implement(IImage::class.java, DockerImage::class.java)
                .build(IImage.Factory::class.java)
        )

        install(
            FactoryModuleBuilder()
                .implement(IContainer::class.java, DockerContainer::class.java)
                .build(IContainer.Factory::class.java)
        )

        //bind(IImage.Factory::class.java).to(LocalImageFactory::class.java)
        //bind(IContainer.Factory::class.java).to(LocalContainerFactory::class.java)
    }

}