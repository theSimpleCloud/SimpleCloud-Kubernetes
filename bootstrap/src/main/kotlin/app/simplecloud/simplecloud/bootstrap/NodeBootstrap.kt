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

import app.simplecloud.simplecloud.node.startup.NodeStartup
import org.apache.logging.log4j.LogManager


/**
 * Created by IntelliJ IDEA.
 * Date: 20/07/2021
 * Time: 11:56
 * @author Frederick Baier
 */

private val logger = LogManager.getLogger(NodeStartup::class.java)

fun main(args: Array<String>) {
    logger.info("Starting SimpleCloud")
    NodeArgumentsParser().main(args)
}