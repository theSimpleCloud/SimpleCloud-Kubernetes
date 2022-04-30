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

package app.simplecloud.simplecloud.kubernetes.api.service

import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.kubernetes.api.pod.KubePod

interface KubeNetworkService {

    fun createService(name: String, serviceSpec: ServiceSpec): KubeService

    fun getService(name: String): KubeService

    /**
     * Translates a service address into an address you can actually connect to
     */
    fun translateAddress(address: Address): Address

    /**
     * Requests a port the given pod wants to bind to
     * @param pod the pod requesting the port
     * @param port the requested port
     * @return the actual port to use
     * @throws java.net.BindException if the requested port is already in use
     */
    fun requestPort(pod: KubePod, port: Int): Int

    class ServiceAlreadyExistException : Exception()

    class AddressTranslationException(msg: String) : Exception(msg)

}
