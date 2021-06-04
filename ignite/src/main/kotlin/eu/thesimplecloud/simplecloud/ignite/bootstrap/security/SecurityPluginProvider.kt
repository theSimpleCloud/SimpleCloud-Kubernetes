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

package eu.thesimplecloud.simplecloud.ignite.bootstrap.security

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