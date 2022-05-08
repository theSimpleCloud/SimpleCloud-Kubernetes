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

import app.simplecloud.simplecloud.api.impl.repository.distributed.predicate.CloudProcessCompareDistributionIdPredicate
import app.simplecloud.simplecloud.api.impl.repository.distributed.predicate.CloudProcessCompareGroupNamePredicate
import app.simplecloud.simplecloud.api.impl.repository.distributed.predicate.CloudProcessCompareUUIDPredicate
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.repository.CloudProcessRepository
import app.simplecloud.simplecloud.distribution.api.Distribution
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 12.06.2021
 * Time: 10:57
 * @author Frederick Baier
 */
class DistributedCloudProcessRepository(
    private val distribution: Distribution
) : AbstractDistributedRepository<String, CloudProcessConfiguration>(
    distribution.getOrCreateCache("cloud-processes")
), CloudProcessRepository {

    override fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<CloudProcessConfiguration> {
        return executeQueryAndFindFirst(CloudProcessCompareUUIDPredicate(uniqueId))
    }

    override fun findProcessByDistributionId(distributionId: UUID): CompletableFuture<CloudProcessConfiguration> {
        return executeQueryAndFindFirst(CloudProcessCompareDistributionIdPredicate(distributionId))
    }

    override fun findProcessesByGroupName(groupName: String): CompletableFuture<Collection<CloudProcessConfiguration>> {
        return executeQuery(CloudProcessCompareGroupNamePredicate(groupName))
    }

}