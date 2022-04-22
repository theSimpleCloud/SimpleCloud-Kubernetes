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

package app.simplecloud.simplecloud.distribution.api

/**
 * Date: 03.04.22
 * Time: 15:25
 * @author Frederick Baier
 *
 */
interface Distribution {

    /**
     * Returns the self component
     * [ClientComponent] if this is a client
     * [ServerComponent] if this is a server
     */
    fun getSelfComponent(): DistributionComponent

    /**
     * Returns all servers currently connected to the cluster
     */
    fun getServers(): List<ServerComponent>

    /**
     * Returns the clients currently connected to this server
     * If this is not a server the list will be empty
     */
    fun getConnectedClients(): List<ClientComponent>

    /**
     * Creates a distributed key value store
     */
    fun <K, V> getOrCreateCache(name: String): Cache<K, V>

    fun getMessageManager(): MessageManager

    fun shutdown()

}