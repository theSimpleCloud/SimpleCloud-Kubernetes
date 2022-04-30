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

package app.simplecloud.simplecloud.kubernetes.api.service

import app.simplecloud.simplecloud.kubernetes.api.Label
import java.util.concurrent.CopyOnWriteArrayList

class ServiceSpec {

    val labels = CopyOnWriteArrayList<Label>()

    @Volatile
    var containerPort: Int = -1
        private set

    @Volatile
    var clusterPort: Int = -1
        private set

    @Volatile
    var publicPort: Int = -1
        private set

    fun withClusterPort(port: Int): ServiceSpec {
        this.clusterPort = port
        return this
    }

    fun withContainerPort(port: Int): ServiceSpec {
        this.containerPort = port
        return this
    }

    fun withPublicPort(port: Int): ServiceSpec {
        this.publicPort = port
        return this
    }

    fun withLabels(vararg labels: Label): ServiceSpec {
        this.labels.addAll(labels)
        return this
    }

}