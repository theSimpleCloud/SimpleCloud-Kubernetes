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

package app.simplecloud.simplecloud.plugin.startup

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.service.CloudProcessService
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.*
import java.util.concurrent.CompletableFuture

@Singleton
class SelfProcessProvider @Inject constructor(
    private val processService: CloudProcessService
) {

    private val internalProcessId = UUID.fromString(System.getenv("SIMPLECLOUD_PROCESS_ID"))

    fun getSelfProcess(): CompletableFuture<CloudProcess> {
        return this.processService.findByUniqueId(this.internalProcessId)
    }

}