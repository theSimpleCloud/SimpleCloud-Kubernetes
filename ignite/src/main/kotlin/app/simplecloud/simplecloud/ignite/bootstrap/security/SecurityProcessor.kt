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
import org.apache.ignite.internal.GridKernalContext
import org.apache.ignite.internal.processors.GridProcessorAdapter
import org.apache.ignite.internal.processors.security.GridSecurityProcessor
import org.apache.ignite.internal.processors.security.SecurityContext
import org.apache.ignite.plugin.PluginProvider
import org.apache.ignite.plugin.security.*
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
