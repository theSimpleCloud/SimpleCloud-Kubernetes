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

package app.simplecloud.simplecloud.module.api.impl.resourcedefinition.builder

import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.ResourceVersionActionsImpl
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceVersionActions
import app.simplecloud.simplecloud.module.api.resourcedefinition.builder.ResourceVersionActionsBuilder

/**
 * Date: 10.02.23
 * Time: 12:11
 * @author Frederick Baier
 *
 */
class ResourceVersionActionBuilderImpl : ResourceVersionActionsBuilder {

    @Volatile
    private var isUpdateDisabled: Boolean = false

    @Volatile
    private var isCreateDisabled: Boolean = false

    @Volatile
    private var isDeleteDisabled: Boolean = false

    @Volatile
    private var updateActionName: String = "update"

    @Volatile
    private var createActionName: String = "create"

    @Volatile
    private var deleteActionName: String = "delete"

    override fun disableCreate(): ResourceVersionActionsBuilder {
        this.isCreateDisabled = true
        return this
    }

    override fun disableUpdate(): ResourceVersionActionsBuilder {
        this.isUpdateDisabled = true
        return this
    }

    override fun disableDelete(): ResourceVersionActionsBuilder {
        this.isDeleteDisabled = true
        return this
    }

    override fun setCreateActionName(name: String): ResourceVersionActionsBuilder {
        this.createActionName = name
        return this
    }

    override fun setUpdateActionName(name: String): ResourceVersionActionsBuilder {
        this.updateActionName = name
        return this
    }

    override fun setDeleteActionName(name: String): ResourceVersionActionsBuilder {
        this.deleteActionName = name
        return this
    }

    override fun build(): ResourceVersionActions {
        return ResourceVersionActionsImpl(
            this.isUpdateDisabled,
            this.isCreateDisabled,
            this.isDeleteDisabled,
            this.updateActionName,
            this.createActionName,
            this.deleteActionName
        )
    }

}