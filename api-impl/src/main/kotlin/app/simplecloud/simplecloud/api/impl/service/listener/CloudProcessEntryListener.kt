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

package app.simplecloud.simplecloud.api.impl.service.listener

import app.simplecloud.simplecloud.api.event.process.CloudProcessRegisteredEvent
import app.simplecloud.simplecloud.api.event.process.CloudProcessUnregisteredEvent
import app.simplecloud.simplecloud.api.event.process.CloudProcessUpdatedEvent
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.distribution.api.EntryListener
import app.simplecloud.simplecloud.eventapi.EventManager

/**
 * Date: 08.05.22
 * Time: 18:33
 * @author Frederick Baier
 *
 */
class CloudProcessEntryListener(
    private val cloudProcessService: CloudProcessService,
    private val eventManager: EventManager,
    private val factory: CloudProcessFactory
) : EntryListener<String, CloudProcessConfiguration> {

    override fun entryAdded(entry: Pair<String, CloudProcessConfiguration>) {
        val cloudProcess = this.factory.create(entry.second, this.cloudProcessService)
        this.eventManager.call(CloudProcessRegisteredEvent(cloudProcess))
    }

    override fun entryUpdated(entry: Pair<String, CloudProcessConfiguration>) {
        val cloudProcess = this.factory.create(entry.second, this.cloudProcessService)
        this.eventManager.call(CloudProcessUpdatedEvent(cloudProcess))
    }

    override fun entryRemoved(entry: Pair<String, CloudProcessConfiguration>) {
        val cloudProcess = this.factory.create(entry.second, this.cloudProcessService)
        this.eventManager.call(CloudProcessUnregisteredEvent(cloudProcess))
    }
}