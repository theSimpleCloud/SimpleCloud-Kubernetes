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

package app.simplecloud.simplecloud.node.api.processtemplate

import app.simplecloud.simplecloud.api.service.ProcessTemplateService
import app.simplecloud.simplecloud.api.template.ProcessTemplate
import app.simplecloud.simplecloud.module.api.impl.NodeCloudAPIImpl
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 19.08.22
 * Time: 09:12
 * @author Frederick Baier
 *
 */
abstract class NodeAPIProcessTemplateCreateTest : ProcessTemplateCreateBaseTest() {

    private val nodeAPIBaseTest = NodeAPIBaseTest()

    @BeforeEach
    override fun setUp() {
        nodeAPIBaseTest.setUp()
        super.setUp()
    }

    @AfterEach
    fun tearDown() {
        nodeAPIBaseTest.tearDown()
    }

    override fun getProcessTemplateService(): ProcessTemplateService<out ProcessTemplate> {
        return getProcessTemplateService(nodeAPIBaseTest.cloudAPI)
    }

    abstract fun getProcessTemplateService(cloudAPI: NodeCloudAPIImpl): ProcessTemplateService<out ProcessTemplate>

}