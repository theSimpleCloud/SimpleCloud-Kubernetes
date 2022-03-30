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

package app.simplecloud.simplecloud.plugin.proxy.request.handler

import app.simplecloud.simplecloud.api.internal.request.process.InternalProcessUpdateRequest
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.plugin.startup.SelfProcessProvider
import app.simplecloud.simplecloud.plugin.util.OnlineCountProvider

/**
 * Date: 30.03.22
 * Time: 13:32
 * @author Frederick Baier
 *
 */
class OnlineCountUpdater(
    private val selfProcessProvider: SelfProcessProvider,
    private val onlineCountProvider: OnlineCountProvider
) {

    fun updateOnlineCount() {
        val selfProcessFuture = this.selfProcessProvider.getSelfProcess()
        selfProcessFuture.thenApply { updateOnlineCount0(it) }
    }

    private fun updateOnlineCount0(selfProcess: CloudProcess) {
        val updateRequest = selfProcess.createUpdateRequest()
        updateRequest as InternalProcessUpdateRequest
        updateRequest.setOnlinePlayers(this.onlineCountProvider.getOnlineCount())
        updateRequest.submit()
    }

}