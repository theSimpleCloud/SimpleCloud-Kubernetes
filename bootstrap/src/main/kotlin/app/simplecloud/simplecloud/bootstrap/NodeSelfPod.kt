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

package app.simplecloud.simplecloud.bootstrap

import app.simplecloud.simplecloud.kubernetes.api.Label
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod
import app.simplecloud.simplecloud.kubernetes.api.pod.PodSpec

/**
 * Date: 25.05.22
 * Time: 18:54
 * @author Frederick Baier
 *
 */
class NodeSelfPod : KubePod {

    override fun getName(): String {
        return "<self-pod>"
    }

    override fun execute(command: String) {
        TODO("Not yet implemented")
    }

    override fun start(podSpec: PodSpec) {
        TODO("Not yet implemented")
    }

    override fun isActive(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isFailed(): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun forceShutdown() {
        TODO("Not yet implemented")
    }

    override fun exists(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLogs(): String {
        TODO("Not yet implemented")
    }

    override fun getLabels(): List<Label> {
        return emptyList()
    }

}