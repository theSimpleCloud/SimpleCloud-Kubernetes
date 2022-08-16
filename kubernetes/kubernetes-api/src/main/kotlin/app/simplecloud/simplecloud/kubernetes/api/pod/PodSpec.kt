/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.kubernetes.api.pod

import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.volume.KubeVolumeClaim
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 12/08/2021
 * Time: 12:02
 * @author Frederick Baier
 */
class PodSpec {

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

    var restartPolicy: String = "OnFailure"
        private set

    fun withContainerPort(containerPort: Int): PodSpec {
        this.containerPort = containerPort
        return this
    }

    fun withLabels(vararg labels: Label): PodSpec {
        this.labels.addAll(labels)
        return this
    }

    fun withEnvironmentVariables(vararg envs: EnvironmentVariable): PodSpec {
        this.environmentVariables.addAll(envs)
        return this
    }

    fun withVolumes(vararg volumes: MountableVolume): PodSpec {
        this.volumes.addAll(volumes)
        return this
    }

    fun withImage(image: String): PodSpec {
        this.image = image
        return this
    }

    /**
     * Sets the max memory in MB
     */
    fun withMaxMemory(maxMemory: Int): PodSpec {
        require(maxMemory > 100) { "MaxMemory must be greater than 100" }
        this.maxMemory = maxMemory
        return this
    }

    fun withRestartPolicy(restartPolicy: String): PodSpec {
        this.restartPolicy = restartPolicy
        return this
    }

    class EnvironmentVariable(
        val name: String,
        val value: String,
    )

    class MountableVolume(
        val volumeClaim: KubeVolumeClaim,
        val mountPath: String,
    )

}