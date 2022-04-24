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

package app.simplecloud.simplecloud.node.startup.task

import app.simplecloud.simplecloud.database.api.DatabaseOfflineCloudPlayerRepository
import app.simplecloud.simplecloud.node.startup.setup.task.FirstWebUserSetup
import app.simplecloud.simplecloud.restserver.auth.JwtTokenHandler
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import com.google.inject.Inject

/**
 * Date: 22.03.22
 * Time: 13:34
 * @author Frederick Baier
 *
 */
class FirstAccountCheck @Inject constructor(
    private val cloudPlayerRepository: DatabaseOfflineCloudPlayerRepository,
    private val tokenHandler: JwtTokenHandler,
    private val restSetupManager: RestSetupManager
) {

    fun checkForAccount() {
        val count = this.cloudPlayerRepository.count().join()
        if (count == 0L) {
            return executeFirstUserSetup()
        }
    }

    private fun executeFirstUserSetup() {
        return FirstWebUserSetup(this.restSetupManager, this.cloudPlayerRepository, this.tokenHandler).executeSetup()
    }

}