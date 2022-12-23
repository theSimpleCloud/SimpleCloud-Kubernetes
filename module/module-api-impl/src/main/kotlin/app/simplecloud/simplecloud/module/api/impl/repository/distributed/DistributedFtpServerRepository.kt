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

package app.simplecloud.simplecloud.module.api.impl.repository.distributed

import app.simplecloud.simplecloud.api.impl.repository.distributed.AbstractDistributedRepository
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.module.api.internal.ftp.configuration.FtpServerConfiguration
import app.simplecloud.simplecloud.module.api.internal.repository.FtpServerRepository

/**
 * Date: 21.12.22
 * Time: 12:14
 * @author Frederick Baier
 *
 */
class DistributedFtpServerRepository(
    private val distribution: Distribution,
) : AbstractDistributedRepository<String, FtpServerConfiguration>(
    distribution.getOrCreateCache("cloud-ftp-server")
), FtpServerRepository