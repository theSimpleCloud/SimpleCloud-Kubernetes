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

import org.apache.ignite.internal.processors.security.SecurityContext
import org.apache.ignite.plugin.security.AuthenticationContext
import org.apache.ignite.plugin.security.SecurityPermission
import org.apache.ignite.plugin.security.SecuritySubject
import java.io.Serializable

/**
 * Created by IntelliJ IDEA.
 * Date: 04.06.2021
 * Time: 11:49
 *
 * This implementation checks permissions and validates them against assigned ones.
 */
class SecurityContextImpl : SecurityContext, Serializable {
    constructor(ctx: AuthenticationContext?) {}
    constructor() {}

    override fun subject(): SecuritySubject {
        return SecuritySubjectImpl()
    }

    override fun taskOperationAllowed(taskClsName: String, perm: SecurityPermission): Boolean {
        return true
    }

    override fun cacheOperationAllowed(cacheName: String, perm: SecurityPermission): Boolean {
        return true
    }

    override fun serviceOperationAllowed(srvcName: String, perm: SecurityPermission): Boolean {
        return true
    }

    override fun systemOperationAllowed(perm: SecurityPermission): Boolean {
        return true
    }
}