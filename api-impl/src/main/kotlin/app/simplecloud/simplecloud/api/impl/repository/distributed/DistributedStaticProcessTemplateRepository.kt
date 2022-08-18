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

package app.simplecloud.simplecloud.api.impl.repository.distributed

import app.simplecloud.simplecloud.api.repository.StaticProcessTemplateRepository
import app.simplecloud.simplecloud.api.template.configuration.AbstractProcessTemplateConfiguration
import app.simplecloud.simplecloud.distribution.api.Distribution

/**
 * Created by IntelliJ IDEA.
 * Date: 27.03.2021
 * Time: 13:42
 * @author Frederick Baier
 */
class DistributedStaticProcessTemplateRepository(
    private val distribution: Distribution,
) : AbstractDistributedRepository<String, AbstractProcessTemplateConfiguration>(
    distribution.getOrCreateCache("cloud-static-templates")
), StaticProcessTemplateRepository