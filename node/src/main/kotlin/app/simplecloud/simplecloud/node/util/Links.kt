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

package app.simplecloud.simplecloud.node.util

import app.simplecloud.simplecloud.api.template.ProcessTemplateType
import app.simplecloud.simplecloud.api.template.group.CloudProcessGroup

/**
 * Date: 31.03.23
 * Time: 18:58
 * @author Frederick Baier
 *
 */
object Links {

    const val ONLINE_COUNT_PROXY_LINK = "OnlineCountProxyLink"
    const val ONLINE_COUNT_LOBBY_LINK = "OnlineCountLobbyLink"
    const val ONLINE_COUNT_SERVER_LINK = "OnlineCountServerLink"

    fun getOnlineCountLinkTypeByGroup(group: CloudProcessGroup): String {
        return when (group.getProcessTemplateType()) {
            ProcessTemplateType.PROXY -> ONLINE_COUNT_PROXY_LINK
            ProcessTemplateType.LOBBY -> ONLINE_COUNT_LOBBY_LINK
            ProcessTemplateType.SERVER -> ONLINE_COUNT_SERVER_LINK
        }
    }

}