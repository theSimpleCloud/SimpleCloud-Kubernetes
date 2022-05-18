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

package app.simplecloud.simplecloud.kubernetest.test.service

import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.service.KubeService
import app.simplecloud.simplecloud.kubernetes.api.service.ServiceSpec

class TestKubeService(
    private val name: String,
    private val serviceSpec: ServiceSpec,
    private val kubeNetworkService: TestKubeNetworkService
) : KubeService {

    override fun getName(): String {
        return this.name
    }

    override fun getContainerPort(): Int {
        return this.serviceSpec.containerPort
    }

    override fun getClusterPort(): Int {
        return this.serviceSpec.clusterPort
    }

    override fun getLabels(): List<Label> {
        return this.serviceSpec.labels
    }

    override fun delete() {
        kubeNetworkService.delete(this)
    }

}
