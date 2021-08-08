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

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoClientSettings
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoClients
import dev.morphia.Datastore
import eu.thesimplecloud.simplecloud.task.Task
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitter
import java.util.concurrent.CompletableFuture
import dev.morphia.Morphia
import org.bson.internal.CodecRegistryHelper


/**
 * Created by IntelliJ IDEA.
 * Date: 05/08/2021
 * Time: 10:01
 * @author Frederick Baier
 */
class MongoDbStartTask(
    private val connectionString: String
) : Task<Datastore>() {

    override fun getName(): String {
        return "start_mongo_client"
    }

    override fun run(): CompletableFuture<Datastore> {
        val morphia = Morphia()
        val mongoClientURI = createMongoClientURI()
        val mongoClient = createMongoClient(mongoClientURI)
        val datastore = morphia.createDatastore(mongoClient, mongoClientURI.database)
        return CompletableFuture.completedFuture(datastore)
    }

    private fun createMongoClient(mongoClientURI: MongoClientURI): MongoClient {
        return MongoClient(mongoClientURI)
    }

    private fun createMongoClientURI(): MongoClientURI {
        return MongoClientURI(
            this.connectionString,
            MongoClientOptions.Builder()
                .serverSelectionTimeout(5000)
        )
    }

}