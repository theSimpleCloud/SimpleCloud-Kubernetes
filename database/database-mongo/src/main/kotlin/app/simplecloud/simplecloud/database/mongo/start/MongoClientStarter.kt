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

package app.simplecloud.simplecloud.database.mongo.start

import app.simplecloud.simplecloud.database.mongo.start.codec.*
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
class MongoClientStarter(
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