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

package app.simplecloud.simplecloud.module.api.resourcedefinition.link

/**
 * Date: 29.03.23
 * Time: 10:20
 * @author Frederick Baier
 *
 */
interface LinkDefinitionBuilder {

    fun setName(name: String): LinkDefinitionBuilder

    fun setOneResourceGroup(group: String): LinkDefinitionBuilder

    fun setOneResourceKind(kind: String): LinkDefinitionBuilder

    fun setManyResourceGroup(group: String): LinkDefinitionBuilder

    fun setManyResourceKind(kind: String): LinkDefinitionBuilder

    fun build(): LinkDefinition

}