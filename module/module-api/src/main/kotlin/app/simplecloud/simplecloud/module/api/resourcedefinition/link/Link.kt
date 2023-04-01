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

import app.simplecloud.simplecloud.api.resourcedefinition.link.LinkConfiguration

/**
 * Date: 29.03.23
 * Time: 10:14
 * @author Frederick Baier
 *
 */
interface Link {

    /**
     * Returns the definition name
     */
    fun getLinkType(): String

    fun getOneResourceName(): String

    fun getManyResourceName(): String

    fun toConfiguration(): LinkConfiguration

}