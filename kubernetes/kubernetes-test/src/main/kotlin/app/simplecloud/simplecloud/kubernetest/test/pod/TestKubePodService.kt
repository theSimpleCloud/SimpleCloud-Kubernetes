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

package app.simplecloud.simplecloud.kubernetest.test.pod

import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePodService
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec
import java.util.concurrent.CopyOnWriteArrayList

class TestKubePodService : KubePodService {

    private val pods = CopyOnWriteArrayList<KubePod>()

    override fun getPod(name: String): KubePod {
        return this.pods.first { it.getName() == name.lowercase() }
    }

    override fun createPod(name: String, podSpec: PodSpec): KubePod {
        val kubePod = TestKubePod(name.lowercase(), this)
        kubePod.start(podSpec)
        this.pods.add(kubePod)
        return kubePod
    }

    fun delete(kubePod: TestKubePod) {
        this.pods.remove(kubePod)
    }

    fun findPodsByLabel(label: Label): List<KubePod> {
        return this.pods.filter { it.getLabels().contains(label) }
    }

}
