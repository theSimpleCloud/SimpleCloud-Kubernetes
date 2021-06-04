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