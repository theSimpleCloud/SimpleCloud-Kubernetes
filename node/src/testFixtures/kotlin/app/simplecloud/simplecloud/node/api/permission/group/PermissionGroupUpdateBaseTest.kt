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

package app.simplecloud.simplecloud.node.api.permission.group

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.api.permission.PermissionEntity
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.request.permission.PermissionEntityUpdateRequest
import app.simplecloud.simplecloud.api.service.PermissionGroupService
import app.simplecloud.simplecloud.node.api.permission.PermissionEntityUpdateBaseTest
import org.junit.jupiter.api.BeforeEach

/**
 * Date: 20.08.22
 * Time: 21:42
 * @author Frederick Baier
 *
 */
abstract class PermissionGroupUpdateBaseTest : PermissionEntityUpdateBaseTest() {

    private lateinit var existingGroup: PermissionGroup
    private lateinit var groupService: PermissionGroupService

    abstract override fun getCloudAPI(): CloudAPI

    @BeforeEach
    override fun setUp() {
        this.groupService = getCloudAPI().getPermissionGroupService()
        this.existingGroup =
            this.groupService.createCreateRequest(createPermissionGroupConfiguration("ExistingGroup")).submit().join()
        super.setUp()
    }

    override fun fetchDefaultPermissionEntity(): PermissionEntity {
        return this.groupService.findByName(this.existingGroup.getName()).join()
    }

    override fun createUpdateRequest(entity: PermissionEntity): PermissionEntityUpdateRequest {
        return this.groupService.createUpdateRequest(entity as PermissionGroup)
    }

}