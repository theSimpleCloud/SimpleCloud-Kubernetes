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

package app.simplecloud.simplecloud.api.impl.service

import app.simplecloud.simplecloud.api.cache.CacheHandler
import app.simplecloud.simplecloud.api.internal.service.InternalCloudStateService
import app.simplecloud.simplecloud.api.utils.CloudState
import java.util.concurrent.CompletableFuture

/**
 * Date: 23.03.23
 * Time: 13:22
 * @author Frederick Baier
 *
 */
class DefaultCloudStateService(
    private val cacheHandler: CacheHandler,
) : InternalCloudStateService {

    private val singletonCache = cacheHandler.getOrCreateSingletonCache<CloudState>("cloud-state")

    override fun setCloudState(state: CloudState) {
        return this.singletonCache.setValue(state)
    }

    override fun getCloudState(): CompletableFuture<CloudState> {
        return this.singletonCache.getValue()
    }
}