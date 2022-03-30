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

package app.simplecloud.simplecloud.plugin.startup

import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.process.state.ProcessState
import com.google.inject.Inject
import org.apache.ignite.Ignite

class SelfIgniteProcessUpdater @Inject constructor(
    private val ignite: Ignite,
    private val selfProcessProvider: SelfProcessProvider
) {

    private val igniteSelfId = this.ignite.cluster().localNode().id()

    fun updateProcessInIgniteBlocking() {
        val cloudProcess = this.selfProcessProvider.getSelfProcess().join()
        val updateRequest = cloudProcess.createUpdateRequest()
        updateRequest as InternalProcessUpdateRequest
        updateRequest.setIgniteId(this.igniteSelfId)
        updateRequest.setState(ProcessState.ONLINE)
        updateRequest.submit().join()
    }

}