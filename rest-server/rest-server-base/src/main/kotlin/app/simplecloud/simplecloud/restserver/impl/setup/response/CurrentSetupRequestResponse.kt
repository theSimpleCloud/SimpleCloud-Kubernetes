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

package app.simplecloud.simplecloud.restserver.impl.setup.response

import app.simplecloud.simplecloud.restserver.api.setup.Setup

/**
 * Created by IntelliJ IDEA.
 * Date: 09/08/2021
 * Time: 22:22
 * @author Frederick Baier
 */
open class CurrentSetupRequestResponse(currentSetup: Setup<*>) {
    val nextSetup = currentSetup.setupName
    val content = currentSetup.additionalContent
}