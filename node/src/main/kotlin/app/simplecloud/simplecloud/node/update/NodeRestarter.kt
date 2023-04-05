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

package app.simplecloud.simplecloud.node.update

import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.internal.service.InternalCloudStateService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.utils.CloudState
import app.simplecloud.simplecloud.module.api.internal.service.InternalFtpServerService
import java.util.concurrent.TimeUnit

/**
 * Date: 01.01.23
 * Time: 16:30
 * @author Frederick Baier
 *
 */
class NodeRestarter(
    private val stateService: InternalCloudStateService,
    private val ftpServerService: InternalFtpServerService,
    private val processService: CloudProcessService,
    private val messageChannelProvider: InternalMessageChannelProvider,
) {

    fun canPerformRestart(): Boolean {
        return this.stateService.getCloudState().get() != CloudState.DISABLED
    }

    fun restartNodes() {
        NodeDisabler(
            this.stateService,
            this.ftpServerService,
            this.processService
        ).disableNodes()
        val restartMessageChannel = this.messageChannelProvider.getInternalRestartMessageChannel()
        //send time to stop node
        restartMessageChannel.createMessageRequestToAll(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3))
            .submit()
    }

}