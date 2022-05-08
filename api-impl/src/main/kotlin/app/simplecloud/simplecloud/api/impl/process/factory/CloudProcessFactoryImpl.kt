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

package app.simplecloud.simplecloud.api.impl.process.factory

import app.simplecloud.simplecloud.api.impl.process.CloudProcessImpl
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessService

/**
 * Date: 06.05.22
 * Time: 21:31
 * @author Frederick Baier
 *
 */
class CloudProcessFactoryImpl : CloudProcessFactory {

    override fun create(configuration: CloudProcessConfiguration, processService: CloudProcessService): CloudProcess {
        return CloudProcessImpl(configuration, processService)
    }
}