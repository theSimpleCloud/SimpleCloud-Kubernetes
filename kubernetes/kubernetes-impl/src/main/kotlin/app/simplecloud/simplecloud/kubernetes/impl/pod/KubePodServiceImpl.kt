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

package app.simplecloud.simplecloud.kubernetes.impl.pod

import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePodService
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import io.kubernetes.client.openapi.apis.CoreV1Api

/**
 * Date: 28.04.22
 * Time: 18:03
 * @author Frederick Baier
 *
 */
class KubePodServiceImpl(
    private val api: CoreV1Api
) : KubePodService {

    override fun getPod(name: String): KubePod {
        val kubePod = KubePodImpl(name, this.api)
        if (!kubePod.isRunning())
            throw NoSuchElementException("No Pod found by name '$name'")
        return kubePod
    }

    override fun createPod(name: String, podSpec: PodSpec): KubePod {
        val pod = KubePodImpl(name, this.api)
        pod.start(podSpec)
        return pod
    }
}