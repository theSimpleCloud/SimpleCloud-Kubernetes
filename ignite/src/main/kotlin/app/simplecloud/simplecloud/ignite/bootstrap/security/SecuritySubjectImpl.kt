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

import org.apache.ignite.plugin.security.SecurityPermissionSet
import org.apache.ignite.plugin.security.SecuritySubject
import org.apache.ignite.plugin.security.SecuritySubjectType
import java.net.InetSocketAddress
import java.util.*


/**
 * Created by IntelliJ IDEA.
 * Date: 04.06.2021
 * Time: 12:01
 *
 * This implementation defines subject information with permissions
 */
class SecuritySubjectImpl : SecuritySubject {
    override fun id(): UUID? {
        return null
    }

    override fun type(): SecuritySubjectType? {
        return null
    }

    override fun login(): Any? {
        return null
    }

    override fun address(): InetSocketAddress? {
        return null
    }

    override fun permissions(): SecurityPermissionSet {
        return SecurityPermissionsSetImpl()
    }
}