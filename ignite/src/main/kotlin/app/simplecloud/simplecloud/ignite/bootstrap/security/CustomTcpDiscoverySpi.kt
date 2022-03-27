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

package app.simplecloud.simplecloud.ignite.bootstrap.security

import org.apache.ignite.internal.IgniteNodeAttributes
import org.apache.ignite.plugin.security.SecurityCredentials
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi

/**
 * Created by IntelliJ IDEA.
 * Date: 04.06.2021
 * Time: 11:46
 */
class CustomTcpDiscoverySpi : TcpDiscoverySpi() {
    private var securityCredentials: SecurityCredentials? = null

    /**
     * This method sets [SecurityCredentials]
     *
     * @param securityCredentials - security credentials
     * @return
     */
    fun setSecurityCredentials(securityCredentials: SecurityCredentials?): CustomTcpDiscoverySpi {
        this.securityCredentials = securityCredentials
        return this
    }

    override fun initLocalNode(srvPort: Int, addExtAddrAttr: Boolean) {
        try {
            super.initLocalNode(srvPort, addExtAddrAttr)
            this.setSecurityCredentials()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSecurityCredentials() {
        if (securityCredentials != null) {
            val attributes: MutableMap<String, Any> = HashMap(locNode.attributes)
            attributes[IgniteNodeAttributes.ATTR_SECURITY_CREDENTIALS] = securityCredentials!!
            locNode.attributes = attributes
        }
    }
}
