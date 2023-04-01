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

package app.simplecloud.simplecloud.module.api.impl.resourcedefinition.link

import app.simplecloud.simplecloud.module.api.resourcedefinition.link.LinkDefinition
import app.simplecloud.simplecloud.module.api.resourcedefinition.link.LinkDefinitionBuilder

/**
 * Date: 29.03.23
 * Time: 10:43
 * @author Frederick Baier
 *
 */
class LinkDefinitionBuilderImpl : LinkDefinitionBuilder {
    private var name: String? = null
    private var oneResourceGroup: String? = null
    private var oneResourceKind: String? = null
    private var manyResourceGroup: String? = null
    private var manyResourceKind: String? = null

    override fun setName(name: String): LinkDefinitionBuilder {
        this.name = name
        return this
    }

    override fun setOneResourceGroup(group: String): LinkDefinitionBuilder {
        this.oneResourceGroup = group
        return this
    }

    override fun setOneResourceKind(kind: String): LinkDefinitionBuilder {
        this.oneResourceKind = kind
        return this
    }

    override fun setManyResourceGroup(group: String): LinkDefinitionBuilder {
        this.manyResourceGroup = group
        return this
    }

    override fun setManyResourceKind(kind: String): LinkDefinitionBuilder {
        this.manyResourceKind = kind
        return this
    }

    override fun build(): LinkDefinition {
        requireNotNull(name) { "Name must be set" }
        requireNotNull(oneResourceGroup) { "One resource group must be set" }
        requireNotNull(oneResourceKind) { "One resource kind must be set" }
        requireNotNull(manyResourceGroup) { "Many resource group must be set" }
        requireNotNull(manyResourceKind) { "Many resource kind must be set" }

        return LinkDefinitionImpl(
            name!!,
            oneResourceGroup!!,
            oneResourceKind!!,
            manyResourceGroup!!,
            manyResourceKind!!
        )
    }
}

