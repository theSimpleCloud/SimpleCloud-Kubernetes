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

package app.simplecloud.simplecloud.kubernetes.impl.deployment

import app.simplecloud.simplecloud.kubernetes.api.deployment.KubeDeployment
import app.simplecloud.simplecloud.kubernetes.api.deployment.KubeDeploymentService
import app.simplecloud.simplecloud.kubernetes.api.exception.KubeException
import io.kubernetes.client.openapi.apis.AppsV1Api

/**
 * Date: 29.12.22
 * Time: 00:00
 * @author Frederick Baier
 *
 */
class KubeDeploymentServiceImpl(
    private val api: AppsV1Api,
) : KubeDeploymentService {

    override fun getDeployment(name: String): KubeDeployment {
        try {
            return KubeDeploymentImpl(name.lowercase(), this.api)
        } catch (e: KubeException) {
            throw NoSuchElementException("Deployment '${name}' does not exist")
        }
    }

}