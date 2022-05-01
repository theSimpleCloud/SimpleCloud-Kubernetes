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

package app.simplecloud.simplecloud.node.startup.prepare.database

import app.simplecloud.simplecloud.database.api.DatabaseCloudProcessGroupRepository
import app.simplecloud.simplecloud.database.api.DatabaseOfflineCloudPlayerRepository
import app.simplecloud.simplecloud.database.api.DatabaseOnlineCountStrategyRepository
import app.simplecloud.simplecloud.database.api.DatabasePermissionGroupRepository
import app.simplecloud.simplecloud.database.api.factory.DatabaseRepositories
import com.google.inject.AbstractModule

/**
 * Date: 24.04.22
 * Time: 12:18
 * @author Frederick Baier
 *
 */
class DatabaseRepositoriesModule(
    private val databaseRepositories: DatabaseRepositories
) : AbstractModule() {

    override fun configure() {
        bind(DatabaseCloudProcessGroupRepository::class.java)
            .toInstance(this.databaseRepositories.cloudProcessGroupRepository)

        bind(DatabaseOfflineCloudPlayerRepository::class.java)
            .toInstance(this.databaseRepositories.offlineCloudPlayerRepository)

        bind(DatabaseOnlineCountStrategyRepository::class.java)
            .toInstance(this.databaseRepositories.onlineCountStrategyRepository)

        bind(DatabasePermissionGroupRepository::class.java)
            .toInstance(this.databaseRepositories.permissionGroupRepository)
    }

}