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

package app.simplecloud.simplecloud.module.api.impl.resourcedefinition

import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceCustomAction
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionActions

/**
 * Date: 10.02.23
 * Time: 12:09
 * @author Frederick Baier
 *
 */
class ResourceVersionActionsImpl(
    private val isUpdateDisabled: Boolean,
    private val isCreateDisabled: Boolean,
    private val isDeleteDisabled: Boolean,
    private val updateActionName: String,
    private val createActionName: String,
    private val deleteActionName: String,
    private val customActions: List<ResourceCustomAction<*>>,
) : ResourceVersionActions {

    override fun isUpdateDisabled(): Boolean {
        return this.isUpdateDisabled
    }

    override fun isCreateDisabled(): Boolean {
        return this.isCreateDisabled
    }

    override fun isDeleteDisabled(): Boolean {
        return this.isDeleteDisabled
    }

    override fun getUpdateActionName(): String {
        return this.updateActionName
    }

    override fun getCreateActionName(): String {
        return this.createActionName
    }

    override fun getDeleteActionName(): String {
        return this.deleteActionName
    }

    override fun getCustomActions(): List<ResourceCustomAction<*>> {
        return this.customActions
    }

    override fun getCustomActionByName(name: String): ResourceCustomAction<*> {
        return this.customActions.firstOrNull { it.getName().equals(name, true) }
            ?: throw NoSuchElementException("Cannot find action by name: $name")
    }

}