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

import app.simplecloud.simplecloud.api.resourcedefinition.Definition
import app.simplecloud.simplecloud.module.api.impl.resourcedefinition.schema.SchemaCreator
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceCustomAction
import app.simplecloud.simplecloud.module.api.resourcedefinition.ResourceCustomActionHandler

/**
 * Date: 14.03.23
 * Time: 13:18
 * @author Frederick Baier
 *
 */
class ResourceCustomActionImpl<T>(
    private val name: String,
    private val bodyClass: Class<T>,
    private val handler: ResourceCustomActionHandler<T>,
) : ResourceCustomAction<T> {

    private val bodySchema = SchemaCreator(bodyClass, false, emptyList()).createSchema()

    override fun getName(): String {
        return this.name
    }

    override fun getBodyClass(): Class<T> {
        return this.bodyClass
    }

    override fun getBodySchema(): Definition {
        return this.bodySchema
    }

    override fun getResourceCustomActionHandler(): ResourceCustomActionHandler<T> {
        return this.handler
    }
}