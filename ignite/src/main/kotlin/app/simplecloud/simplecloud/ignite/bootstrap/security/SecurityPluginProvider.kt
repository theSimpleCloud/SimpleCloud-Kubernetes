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

import org.apache.ignite.IgniteCheckedException
import org.apache.ignite.cluster.ClusterNode
import org.apache.ignite.internal.IgniteKernal
import org.apache.ignite.internal.processors.security.GridSecurityProcessor
import org.apache.ignite.plugin.*
import java.io.Serializable
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 04.06.2021
 * Time: 11:57
 *
 * This is implementation of [PluginProvider] which creates instance of [SecurityPlugin]
 */
/**

 */
class SecurityPluginProvider(val securityPluginConfiguration: SecurityPluginConfiguration) :
    PluginProvider<SecurityPluginConfiguration?> {
    private var authenticationPlugin: IgnitePlugin? = null

    override fun name(): String {
        return "Security Plugin"
    }

    override fun version(): String {
        return "1.0.1"
    }

    override fun copyright(): String {
        return "BugDbug ©2019"
    }

    override fun <T : IgnitePlugin?> plugin(): T {
        return authenticationPlugin as T
    }

    @Throws(IgniteCheckedException::class)
    override fun initExtensions(ctx: PluginContext, registry: ExtensionRegistry) {
        authenticationPlugin = SecurityPlugin()
    }



    override fun <T : Any?> createComponent(pluginContext: PluginContext, cls: Class<T>): T? {
        return if (cls == GridSecurityProcessor::class.java) {
            SecurityProcessor((pluginContext.grid() as IgniteKernal).context()) as T
        } else null
    }

    override fun createCacheProvider(ctx: CachePluginContext<*>?): CachePluginProvider<*>? {
        return null
    }

    @Throws(IgniteCheckedException::class)
    override fun start(ctx: PluginContext) {
    }

    @Throws(IgniteCheckedException::class)
    override fun stop(cancel: Boolean) {
    }

    @Throws(IgniteCheckedException::class)
    override fun onIgniteStart() {
    }

    override fun onIgniteStop(cancel: Boolean) {}
    override fun provideDiscoveryData(nodeId: UUID): Serializable? {
        return null
    }

    override fun receiveDiscoveryData(nodeId: UUID, data: Serializable) {}

    @Throws(PluginValidationException::class)
    override fun validateNewNode(node: ClusterNode) {
    }
}