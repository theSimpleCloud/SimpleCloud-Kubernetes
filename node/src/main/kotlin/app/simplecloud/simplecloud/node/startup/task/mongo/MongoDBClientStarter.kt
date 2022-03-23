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

package app.simplecloud.simplecloud.node.startup.task.mongo

import app.simplecloud.simplecloud.node.startup.task.mongo.codec.*
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import dev.morphia.Datastore
import dev.morphia.Morphia
import org.bson.UuidRepresentation
import org.bson.codecs.configuration.CodecRegistries
import java.util.concurrent.TimeUnit


/**
 * Created by IntelliJ IDEA.
 * Date: 05/08/2021
 * Time: 10:01
 * @author Frederick Baier
 */
class MongoDBClientStarter(
    connectionString: String
) {

    private val connectionString = ConnectionString(connectionString)

    fun startClient(): Datastore {
        println(connectionString.toString())
        val mongoClient = MongoClients.create(createClientSettings())
       return Morphia.createDatastore(mongoClient, this.connectionString.database!!)
    }

    private fun createClientSettings(): MongoClientSettings {
        val addressCodec = AddressCodec()
        val permissionConfigurationCodec = PermissionConfigurationCodec()
        val codecs = CodecRegistries.fromCodecs(
            PlayerWebConfigCodec(),
            PlayerConnectionConfigurationCodec(addressCodec),
            addressCodec,
            PermissionPlayerConfigurationCodec(permissionConfigurationCodec),
            permissionConfigurationCodec
        )
        val codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            codecs
        )
        return MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .codecRegistry(codecRegistry)
            .applyToSocketSettings {
            it.readTimeout(5, TimeUnit.SECONDS)
        }.applyToClusterSettings {
            it.serverSelectionTimeout(5, TimeUnit.SECONDS)
        }.applyConnectionString(this.connectionString).build()
    }

}