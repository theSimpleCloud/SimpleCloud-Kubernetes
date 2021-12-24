/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.api.impl.repository.ignite

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.impl.ignite.predicate.CloudProcessCompareGroupNamePredicate
import eu.thesimplecloud.simplecloud.api.impl.ignite.predicate.CloudProcessCompareUUIDPredicate
import eu.thesimplecloud.simplecloud.api.process.CloudProcessConfiguration
import eu.thesimplecloud.simplecloud.api.repository.CloudProcessRepository
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
    private val ignite: Ignite
) : AbstractIgniteRepository<String, CloudProcessConfiguration>(
    ignite.getOrCreateCache("cloud-processes")
), CloudProcessRepository {

    override fun findProcessByUniqueId(uniqueId: UUID): CompletableFuture<CloudProcessConfiguration> {
        return executeQueryAndFindFirst(CloudProcessCompareUUIDPredicate(uniqueId))
    }

    override fun findProcessesByGroupName(groupName: String): CompletableFuture<List<CloudProcessConfiguration>> {
        return executeQuery(CloudProcessCompareGroupNamePredicate(groupName))
    }


}