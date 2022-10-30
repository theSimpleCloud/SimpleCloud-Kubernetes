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

package app.simplecloud.simplecloud.node.connect

import app.simplecloud.simplecloud.database.api.factory.DatabaseRepositories
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.node.process.unregister.ProcessUnregisterRunnable
import app.simplecloud.simplecloud.node.task.ErrorResolvedCheckerRunnable
import app.simplecloud.simplecloud.node.task.NodeOnlineProcessCheckerRunnable
import java.util.concurrent.TimeUnit

/**
 * Date: 15.08.22
 * Time: 08:23
 * @author Frederick Baier
 *
 */
class ClusterInitializer(
    private val distribution: Distribution,
    private val distributedRepositories: app.simplecloud.simplecloud.node.connect.DistributedRepositories,
    private val databaseRepositories: DatabaseRepositories,
) {
    fun initialize() {
        initializeRepositories()
        initializeSchedulers()
    }

    private fun initializeSchedulers() {
        val scheduler = this.distribution.getScheduler("system")
        scheduler.scheduleAtFixedRate(ProcessUnregisterRunnable(), 1, 1, TimeUnit.SECONDS)
        scheduler.scheduleAtFixedRate(NodeOnlineProcessCheckerRunnable(), 1, 1, TimeUnit.SECONDS)
        scheduler.scheduleAtFixedRate(ErrorResolvedCheckerRunnable(), 1, 1, TimeUnit.SECONDS)
    }

    private fun initializeRepositories() {
        val nodeRepositoriesInitializer = app.simplecloud.simplecloud.node.connect.NodeRepositoriesInitializer(
            distributedRepositories.cloudProcessGroupRepository,
            this.databaseRepositories.cloudProcessGroupRepository,
            distributedRepositories.permissionGroupRepository,
            this.databaseRepositories.permissionGroupRepository,
            distributedRepositories.distributedOnlineCountStrategyRepository,
            this.databaseRepositories.onlineCountStrategyRepository,
            distributedRepositories.staticProcessTemplateRepository,
            this.databaseRepositories.staticProcessTemplateRepository
        )
        nodeRepositoriesInitializer.initializeRepositories()
    }


}