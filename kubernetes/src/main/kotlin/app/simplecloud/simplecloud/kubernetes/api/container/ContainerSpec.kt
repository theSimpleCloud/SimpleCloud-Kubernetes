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

package app.simplecloud.simplecloud.kubernetes.api.container

import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 12/08/2021
 * Time: 12:02
 * @author Frederick Baier
 */
class ContainerSpec {

    val environmentVariables = CopyOnWriteArrayList<EnvironmentVariable>()
    val volumes = CopyOnWriteArrayList<MountableVolume>()
    val labels = CopyOnWriteArrayList<Label>()

    @Volatile
    var maxMemory: Int = -1
        private set

    @Volatile
    var containerPort: Int = -1
        private set

    var image: String? = null
        private set

    fun withContainerPort(containerPort: Int): ContainerSpec {
        this.containerPort = containerPort
        return this
    }

    fun withLabels(vararg labels: Label): ContainerSpec {
        this.labels.addAll(labels)
        return this
    }

    fun withEnvironmentVariables(vararg envs: EnvironmentVariable): ContainerSpec {
        this.environmentVariables.addAll(envs)
        return this
    }

    fun withVolumes(vararg volumes: MountableVolume): ContainerSpec {
        this.volumes.addAll(volumes)
        return this
    }

    fun withImage(image: String): ContainerSpec {
        this.image = image
        return this
    }

    /**
     * Sets the max memory in MB
     */
    fun withMaxMemory(maxMemory: Int): ContainerSpec {
        require(maxMemory > 100) { "MaxMemory must be greater than 100" }
        this.maxMemory = maxMemory
        return this
    }

    class EnvironmentVariable(
        val name: String,
        val value: String
    )

    class MountableVolume(
        val volumeClaim: KubeVolumeClaim,
        val mountPath: String
    )

}