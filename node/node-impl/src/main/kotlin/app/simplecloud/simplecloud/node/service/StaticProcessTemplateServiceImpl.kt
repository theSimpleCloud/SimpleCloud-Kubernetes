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

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedStaticProcessTemplateRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractStaticProcessTemplateService
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.UniversalStaticProcessTemplateFactory
import app.simplecloud.simplecloud.api.template.static.StaticProcessTemplate
import app.simplecloud.simplecloud.database.api.DatabaseStaticProcessTemplateRepository

/**
 * Date: 17.08.22
 * Time: 13:32
 * @author Frederick Baier
 *
 */
class StaticProcessTemplateServiceImpl(
    staticTemplateFactory: UniversalStaticProcessTemplateFactory,
    private val distributedRepository: DistributedStaticProcessTemplateRepository,
    private val databaseRepository: DatabaseStaticProcessTemplateRepository,
) : AbstractStaticProcessTemplateService(distributedRepository, staticTemplateFactory) {

    override suspend fun updateGroupInternal0(template: StaticProcessTemplate) {
        this.distributedRepository.save(template.getName(), template.toConfiguration()).await()
        saveToDatabase(template)
    }

    private fun saveToDatabase(template: StaticProcessTemplate) {
        this.databaseRepository.save(template.getName(), template.toConfiguration())
    }

    override suspend fun deleteStaticTemplateInternal(template: StaticProcessTemplate) {
        this.distributedRepository.remove(template.getName())
        deleteTemplateFromDatabase(template)
    }

    private fun deleteTemplateFromDatabase(template: StaticProcessTemplate) {
        this.databaseRepository.remove(template.getName())
    }


}