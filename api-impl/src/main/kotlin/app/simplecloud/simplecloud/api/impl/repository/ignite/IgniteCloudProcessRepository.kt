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

package app.simplecloud.simplecloud.api.impl.repository.ignite

import app.simplecloud.simplecloud.api.impl.ignite.predicate.CloudProcessCompareGroupNamePredicate
import app.simplecloud.simplecloud.api.impl.ignite.predicate.CloudProcessCompareIgniteIdPredicate
import app.simplecloud.simplecloud.api.impl.ignite.predicate.CloudProcessCompareUUIDPredicate
import app.simplecloud.simplecloud.api.impl.repository.ignite.message.IgniteCacheUpdateMessaging
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.repository.CloudProcessRepository
import com.google.inject.Inject
import com.google.inject.Singleton
import org.apache.ignite.Ignite
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 12.06.2021
 * Time: 10:57
 * @author Frederick Baier
 */
@Singleton
class IgniteCloudProcessRepository @Inject constructor(
    private val ignite: Ignite,
    igniteCacheUpdateMessaging: IgniteCacheUpdateMessaging
) : AbstractIgniteRepository<String, CloudProcessConfiguration>(
    ignite.getOrCreateCache("cloud-processes"),
    igniteCacheUpdateMessaging
), CloudProcessRepository {

    override fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<CloudProcessConfiguration> {
        return executeQueryAndFindFirst(CloudProcessCompareUUIDPredicate(uniqueId))
    }

    override fun findProcessByIgniteId(igniteId: UUID): CompletableFuture<CloudProcessConfiguration> {
        return executeQueryAndFindFirst(CloudProcessCompareIgniteIdPredicate(igniteId))
    }

    override fun findProcessesByGroupName(groupName: String): CompletableFuture<List<CloudProcessConfiguration>> {
        return executeQuery(CloudProcessCompareGroupNamePredicate(groupName))
    }


}