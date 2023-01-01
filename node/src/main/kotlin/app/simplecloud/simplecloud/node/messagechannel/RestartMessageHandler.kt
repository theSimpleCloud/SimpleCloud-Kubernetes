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

package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.impl.env.EnvironmentVariables
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import app.simplecloud.simplecloud.kubernetes.api.KubeAPI
import org.apache.logging.log4j.LogManager
import java.util.concurrent.CompletableFuture

class RestartMessageHandler(
    private val kubeAPI: KubeAPI,
    private val environmentVariables: EnvironmentVariables,
) : MessageHandler<Long, Unit> {

    override fun handleMessage(
        message: Long,
        sender: NetworkComponent,
    ): CompletableFuture<Unit> = CloudScope.future {
        logger.info("Restarting cloud...")
        while (true) {
            if (message <= System.currentTimeMillis()) {
                kubeAPI.getPodService().getPod(environmentVariables.get("HOSTNAME")).delete()
                return@future
            }
            Thread.sleep(10)
        }
    }

    companion object {
        private val logger =
            LogManager.getLogger(RestartMessageHandler::class.java)
    }

}