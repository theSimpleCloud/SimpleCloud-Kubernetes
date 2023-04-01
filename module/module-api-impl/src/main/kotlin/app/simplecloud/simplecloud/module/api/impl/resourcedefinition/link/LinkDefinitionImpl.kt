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

/**
 * Date: 29.03.23
 * Time: 10:43
 * @author Frederick Baier
 *
 */
class LinkDefinitionImpl(
    private val name: String,
    private val oneResourceGroup: String,
    private val oneResourceKind: String,
    private val manyResourceGroup: String,
    private val manyResourceKind: String,
) : LinkDefinition {

    override fun getName(): String {
        return this.name
    }

    override fun getOneResourceGroup(): String {
        return this.oneResourceGroup
    }

    override fun getOneResourceKind(): String {
        return this.oneResourceKind
    }

    override fun getManyResourceGroup(): String {
        return this.manyResourceGroup
    }

    override fun getManyResourceKind(): String {
        return this.manyResourceKind
    }
}
