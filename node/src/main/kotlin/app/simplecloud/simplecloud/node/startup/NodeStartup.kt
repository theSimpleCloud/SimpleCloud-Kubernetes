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

package app.simplecloud.simplecloud.node.startup

import app.simplecloud.simplecloud.node.connect.NodeClusterConnect
import app.simplecloud.simplecloud.node.startup.task.NodePreparer
import com.google.inject.Injector
import dev.morphia.Datastore
import org.apache.logging.log4j.LogManager


/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 10:23
 * @author Frederick Baier
 */
class NodeStartup(
    private val startArguments: NodeStartArgumentParserMain
) {

    fun start() {
        val injector = NodePreparer(this.startArguments).prepare()
        executeClusterConnect(injector)
    }

    private fun executeClusterConnect(injector: Injector) {
        try {
            executeClusterConnect0(injector)
        } catch (e: Exception) {
            logger.error("An error occurred while connecting to cluster", e)
        }
    }

    private fun executeClusterConnect0(injector: Injector) {
        val task = NodeClusterConnect(
            injector,
            injector.getInstance(Datastore::class.java),
        )
        task.connect()
    }

    companion object {
        private val logger = LogManager.getLogger(NodeStartup::class.java)
    }

}