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

package eu.thesimplecloud.simplecloud.kubernetes.container

import eu.thesimplecloud.simplecloud.kubernetes.Label
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 12/08/2021
 * Time: 12:02
 * @author Frederick Baier
 */
class ContainerSpec {

    val volumeBindings = CopyOnWriteArrayList<VolumeBinding>()
    val portBindings = CopyOnWriteArrayList<PortBinding>()
    val environmentVariables = CopyOnWriteArrayList<EnvironmentVariable>()
    val labels = CopyOnWriteArrayList<Label>()

    @Volatile
    var maxMemory: Int = -1
        private set

    @Volatile
    var containerPort: Int = -1
        private set

    fun withPortBinding(hostPort: Int, containerPort: Int): ContainerSpec {
        this.portBindings.add(PortBinding(hostPort, containerPort))
        return this
    }

    fun withLocalVolumeBinding(volume: VolumeBinding): ContainerSpec {
        this.volumeBindings.add(volume)
        return this
    }

    fun withContainerPort(containerPort: Int): ContainerSpec {
        this.containerPort = containerPort
        return this
    }

    fun withLabels(vararg labels: Label): ContainerSpec {
        this.labels.addAll(labels)
        return this
    }

    fun withEnvironmentVariable(env: EnvironmentVariable): ContainerSpec {
        this.environmentVariables.add(env)
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

    class PortBinding(
        val hostPort: Int,
        val containerPort: Int
    )

    class VolumeBinding(
        val hostPath: String,
        val containerPath: String
    )

    class EnvironmentVariable(
        val name: String,
        val value: String
    )

}