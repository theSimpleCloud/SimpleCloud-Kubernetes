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

import org.apache.ignite.IgniteCheckedException
import org.apache.ignite.cluster.ClusterNode
import org.apache.ignite.internal.GridKernalContext
import org.apache.ignite.internal.processors.GridProcessorAdapter
import org.apache.ignite.internal.processors.security.GridSecurityProcessor
import org.apache.ignite.internal.processors.security.SecurityContext
import org.apache.ignite.plugin.PluginProvider
import org.apache.ignite.plugin.security.AuthenticationContext
import org.apache.ignite.plugin.security.SecurityCredentials
import org.apache.ignite.plugin.security.SecurityException
import org.apache.ignite.plugin.security.SecurityPermission
import org.apache.ignite.plugin.security.SecuritySubject
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 04.06.2021
 * Time: 11:57
 *
 * This is implementation of [GridSecurityProcessor] which  handles cluster security
 */
class SecurityProcessor constructor(ctx: GridKernalContext) : GridProcessorAdapter(ctx),
    GridSecurityProcessor {
    private val securityCredentials: SecurityCredentials

    /**
     * @param node
     * @param cred
     * @return
     * @throws IgniteCheckedException
     */
    @Throws(IgniteCheckedException::class)
    override fun authenticateNode(node: ClusterNode, cred: SecurityCredentials): SecurityContext? {
        return if (securityCredentials.login == cred.login && securityCredentials.password == cred.password) {
            SecurityContextImpl()
        } else null
    }

    override fun isGlobalNodeAuthentication(): Boolean {
        return false
    }

    @Throws(IgniteCheckedException::class)
    override fun authenticate(ctx: AuthenticationContext): SecurityContext {
        return SecurityContextImpl(ctx)
    }

    @Throws(IgniteCheckedException::class)
    override fun authenticatedSubjects(): Collection<SecuritySubject> {
        return Arrays.asList(SecuritySubjectImpl())
    }

    @Throws(IgniteCheckedException::class)
    override fun authenticatedSubject(subjId: UUID): SecuritySubject {
        return SecuritySubjectImpl()
    }

    @Throws(SecurityException::class)
    override fun authorize(name: String?, perm: SecurityPermission?, securityCtx: SecurityContext?) {
        //TODO - Here we can check permissions for given cache operation
    }

    override fun onSessionExpired(subjId: UUID) {}
    override fun enabled(): Boolean {
        return true
    }

    /**
     * @param ctx Kernal context.
     */
    init {
        val config = ctx.config()
        val securityPluginProvider = Arrays.stream(config.pluginProviders)
            .filter { pluginProvider: PluginProvider<*>? -> pluginProvider is SecurityPluginProvider }
            .findFirst().orElseThrow() as SecurityPluginProvider
        securityCredentials = securityPluginProvider.securityPluginConfiguration.securityCredentials
    }
}
