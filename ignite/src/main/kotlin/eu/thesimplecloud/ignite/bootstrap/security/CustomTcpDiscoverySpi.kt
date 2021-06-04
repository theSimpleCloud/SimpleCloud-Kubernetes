/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.ignite.bootstrap.security

import org.apache.ignite.internal.IgniteNodeAttributes
import org.apache.ignite.plugin.security.SecurityCredentials
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import java.lang.Exception

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
