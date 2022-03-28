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

package app.simplecloud.simplecloud.node.startup.token

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import org.apache.commons.lang3.RandomStringUtils

/**
 * Date: 19.03.22
 * Time: 10:01
 * @author Frederick Baier
 *
 */
@Entity("token_secret")
class TokenSecretEntity(
    @Id
    val identifier: String = KEY,
    val secret: String = RandomStringUtils.randomAlphanumeric(32),
) {

    companion object {
        const val KEY = "key"
    }

}