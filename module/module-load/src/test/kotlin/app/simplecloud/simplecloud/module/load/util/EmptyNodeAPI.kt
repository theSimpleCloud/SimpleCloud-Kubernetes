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

package app.simplecloud.simplecloud.module.load.util

import app.simplecloud.simplecloud.api.NodeCloudAPI
import app.simplecloud.simplecloud.module.api.LocalNodeAPI
import app.simplecloud.simplecloud.module.api.NodeAPI

/**
 * Date: 04.09.22
 * Time: 09:56
 * @author Frederick Baier
 *
 */
class EmptyNodeAPI : NodeAPI {
    override fun getCloudAPI(): NodeCloudAPI {
        throw UnsupportedOperationException()
    }

    override fun getLocalAPI(): LocalNodeAPI {
        throw UnsupportedOperationException()
    }
}