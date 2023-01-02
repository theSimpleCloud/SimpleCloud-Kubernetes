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
import app.simplecloud.simplecloud.kubernetes.api.exception.KubeException
import io.kubernetes.client.custom.V1Patch
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.util.PatchUtils


/**
 * Date: 29.12.22
 * Time: 00:00
 * @author Frederick Baier
 *
 */
class KubeDeploymentImpl(
    name: String,
    private val api: AppsV1Api,
) : KubeDeployment {

    private val name: String = name.lowercase()

    @Volatile
    private var deployment = readNamespacedDeployment()

    private fun readNamespacedDeployment(): V1Deployment {
        try {
            return this.api.readNamespacedDeployment(this.name, "default", null)
        } catch (ex: ApiException) {
            throw KubeException(ex.responseBody, ex)
        }
    }

    override fun getName(): String {
        return this.name
    }

    override fun editImage(image: String) {
        val jsonUpdateString = generateImageUpdateJson(image)
        println(jsonUpdateString)
        try {
            val deploy2 = PatchUtils.patch(
                V1Deployment::class.java,
                {

                    api.patchNamespacedDeploymentCall(
                        this.name,
                        "default",
                        V1Patch(jsonUpdateString),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    )
                },
                V1Patch.PATCH_FORMAT_JSON_MERGE_PATCH,
                api.apiClient
            )
        } catch (e: ApiException) {
            throw KubeException(e.responseBody, e)
        }
        this.deployment = readNamespacedDeployment()
    }

    override fun getImageName(): String {
        return this.deployment.spec!!.template!!.spec!!.containers.first().image!!
    }

    private fun generateImageUpdateJson(imageName: String): String {
        return "{\"spec\": {\"template\": {\"spec\": {\"containers\": [{\"name\": \"default\",\"image\": \"${imageName}\"}]}}}}"
    }

}