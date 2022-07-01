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

package app.simplecloud.simplecloud.prepare

import java.io.File
import java.lang.instrument.Instrumentation


class AgentMain {

    companion object {

        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation?) {
            val targetFile = File("plugins/SimpleCloud-Plugin.jar")
            Downloader.userAgentDownload("http://content:8008/content/SimpleCloud-Plugin.jar", targetFile)
            targetFile.copyTo(File("extensions/SimpleCloud-Plugin.jar"))
            println("Downloaded plugin file to ${targetFile.absolutePath}")
        }
    }


}