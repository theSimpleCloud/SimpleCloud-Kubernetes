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

package app.simplecloud.simplecloud.node

import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.template.configuration.ProxyProcessTemplateConfiguration
import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.distribution.api.Address
import java.util.*

/**
 * Date: 11.05.22
 * Time: 10:30
 * @author Frederick Baier
 *
 */
class DatabaseFactoryProvider {
    private val databaseFactory = InMemoryRepositorySafeDatabaseFactory()


    fun withFirstUser(): DatabaseFactoryProvider {
        val offlineCloudPlayerRepository = this.databaseFactory.offlineCloudPlayerRepository
        val playerUniqueId = UUID.randomUUID()
        val playerName = "Wetterbericht"
        val configuration = OfflineCloudPlayerConfiguration(
            playerName,
            playerUniqueId,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            0L,
            playerName,
            PlayerConnectionConfiguration(
                playerUniqueId,
                1,
                playerName,
                Address("127.0.0.1", 1900),
                true
            ),
            PlayerWebConfig("123", true),
            PermissionPlayerConfiguration(
                playerUniqueId,
                emptyList()
            )
        )
        offlineCloudPlayerRepository.save(configuration.uniqueId, configuration)
        return this
    }

    fun withProxyGroup(groupName: String): DatabaseFactoryProvider {
        val groupRepository = this.databaseFactory.cloudProcessGroupRepository
        val groupConfiguration = ProxyProcessTemplateConfiguration(
            groupName,
            512,
            20,
            false,
            "image",
            true,
            0,
            null,
            true,
            25565
        )
        groupRepository.save(groupName, groupConfiguration)
        return this
    }

    fun withMinOnlineStrategy(name: String, targetGroup: String, number: Int): DatabaseFactoryProvider {
        val strategyRepository = this.databaseFactory.onlineCountStrategyRepository
        val configuration = ProcessOnlineCountStrategyConfiguration(
            "Min",
            "app.simplecloud.simplecloud.node.onlinestrategy.MinOnlineStrategy",
            setOf(targetGroup),
            mapOf("min" to number.toString())
        )
        strategyRepository.save(configuration.name, configuration)
        return this
    }

    fun get(): InMemoryRepositorySafeDatabaseFactory {
        return this.databaseFactory
    }

}