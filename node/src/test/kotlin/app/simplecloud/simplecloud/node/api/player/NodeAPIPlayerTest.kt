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

package app.simplecloud.simplecloud.node.api.player

import app.simplecloud.simplecloud.api.internal.InternalCloudAPI
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.module.api.resourcedefinition.request.ResourceRequestHandler
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import app.simplecloud.simplecloud.node.util.TestPlayerProvider
import app.simplecloud.simplecloud.node.util.TestProcessProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 15.05.22
 * Time: 19:14
 * @author Frederick Baier
 *
 */
open class NodeAPIPlayerTest : NodeAPIBaseTest(), TestProcessProvider, TestPlayerProvider {

    protected lateinit var cloudPlayerService: InternalCloudPlayerService
    protected lateinit var cloudProcessService: CloudProcessService
    protected lateinit var cloudProcessGroupService: CloudProcessGroupService


    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.cloudPlayerService = this.cloudAPI.getCloudPlayerService()
        this.cloudProcessService = this.cloudAPI.getProcessService()
        this.cloudProcessGroupService = this.cloudAPI.getProcessGroupService()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    override fun getResourceRequestHandler(): ResourceRequestHandler {
        return this.cloudAPI.getResourceRequestHandler()
    }

    override fun getCloudAPI(): InternalCloudAPI {
        return this.cloudAPI
    }

}