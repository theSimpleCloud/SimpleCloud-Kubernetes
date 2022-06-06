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

package app.simplecloud.simplecloud.plugin.proxy

import app.simplecloud.simplecloud.plugin.SelfOnlineCountProvider

/**
 * Date: 29.05.22
 * Time: 19:51
 * @author Frederick Baier
 *
 */
class TestSelfOnlineCountProvider : SelfOnlineCountProvider {

    @Volatile
    private var onlineCount: Int = 0

    override fun getOnlineCount(): Int {
        return this.onlineCount
    }

    fun addToOnlineCount(count: Int) {
        this.onlineCount += count
    }

}