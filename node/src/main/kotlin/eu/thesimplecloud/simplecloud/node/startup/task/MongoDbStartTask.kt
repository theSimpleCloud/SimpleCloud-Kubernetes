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

package eu.thesimplecloud.simplecloud.node.startup.task

import com.mongodb.*
import com.mongodb.client.MongoClients
import dev.morphia.Datastore
import eu.thesimplecloud.simplecloud.task.Task
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitter
import java.util.concurrent.CompletableFuture
import dev.morphia.Morphia
import eu.thesimplecloud.simplecloud.node.repository.ModuleEntity
import org.bson.internal.CodecRegistryHelper
import java.util.concurrent.TimeUnit


/**
 * Created by IntelliJ IDEA.
 * Date: 05/08/2021
 * Time: 10:01
 * @author Frederick Baier
 */
class MongoDbStartTask(
    connectionString: String
) : Task<Datastore>() {

    private val connectionString = ConnectionString(connectionString)

    override fun getName(): String {
        return "start_mongo_client"
    }

    override fun run(): CompletableFuture<Datastore> {
        val mongoClient = MongoClients.create(createClientSettings())
        val datastore = Morphia.createDatastore(mongoClient, this.connectionString.database!!)
        return CompletableFuture.completedFuture(datastore)
    }

    private fun createClientSettings(): MongoClientSettings {
        return MongoClientSettings.builder().applyToSocketSettings {
            it.readTimeout(5, TimeUnit.SECONDS)
        }.applyToClusterSettings {
            it.serverSelectionTimeout(5, TimeUnit.SECONDS)
        }.applyConnectionString(this.connectionString).build()
    }

}