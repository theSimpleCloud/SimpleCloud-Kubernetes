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

package app.simplecloud.simplecloud.module.api.impl

import app.simplecloud.simplecloud.module.api.LocalServiceRegistry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Date: 24.09.22
 * Time: 10:08
 * @author Frederick Baier
 *
 */
class LocalServiceRegistryTest {

    private var registry: LocalServiceRegistry = LocalServiceRegistryImpl()

    @BeforeEach
    fun setUp() {
        this.registry = LocalServiceRegistryImpl()
    }

    @Test
    fun registerService_serviceIsAccessible() {
        val implementation = TestServiceImpl()
        registry.registerService(TestService::class.java, implementation)

        val registeredImplementation = registry.getService(TestService::class.java)
        Assertions.assertTrue(implementation === registeredImplementation)
    }

    @Test
    fun register2Service_servicesAreAccessible() {
        val implementation = TestService2Impl()
        registry.registerService(TestService2::class.java, implementation)
        registry.registerService(TestService::class.java, TestServiceImpl())

        val registeredImplementation = registry.getService(TestService2::class.java)
        Assertions.assertTrue(implementation === registeredImplementation)
    }


    interface TestService

    class TestServiceImpl : TestService

    interface TestService2

    class TestService2Impl : TestService2
}