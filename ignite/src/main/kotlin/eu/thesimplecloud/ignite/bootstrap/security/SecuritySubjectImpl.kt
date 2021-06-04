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

import org.apache.ignite.plugin.security.SecurityPermissionSet

import java.net.InetSocketAddress

import org.apache.ignite.plugin.security.SecuritySubjectType

import java.util.UUID

import org.apache.ignite.plugin.security.SecuritySubject




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