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

package app.simplecloud.simplecloud.node.startup.setup.body

/**
 * Created by IntelliJ IDEA.
 * Date: 05/08/2021
 * Time: 12:00
 * @author Frederick Baier
 */
class MongoSetupResponseBody(
    val connectionString: String,
    val mongoMode: MongoMode
) {

    private constructor(): this("", MongoMode.EXTERNAL)

    enum class MongoMode {

        CREATE,

        EXTERNAL

    }

}