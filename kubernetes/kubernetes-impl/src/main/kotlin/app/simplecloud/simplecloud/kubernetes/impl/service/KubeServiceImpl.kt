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

package app.simplecloud.simplecloud.kubernetes.impl.service

import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.service.KubeService
import io.kubernetes.client.openapi.apis.CoreV1Api

/**
 * Date: 30.04.22
 * Time: 13:04
 * @author Frederick Baier
 *
 */
class KubeServiceImpl(
    private val name: String,
    private val api: CoreV1Api
) : KubeService {

    private val service = this.api.readNamespacedService(this.name, "default", null)

    override fun getName(): String {
        return this.name
    }

    override fun getContainerPort(): Int {
        val servicePort = this.service.spec?.ports?.first() ?: return -1
        return servicePort.targetPort?.intValue ?: -1
    }

    override fun getClusterPort(): Int {
        val servicePort = this.service.spec?.ports?.first() ?: return -1
        return servicePort.port
    }

    override fun getLabels(): List<Label> {
        return this.service.metadata?.labels?.map { Label(it.key, it.value) } ?: emptyList()
    }

    override fun delete() {
        this.api.deleteNamespacedService(
            this.name,
            "default",
            null,
            null,
            null,
            null,
            null,
            null
        )
    }
}