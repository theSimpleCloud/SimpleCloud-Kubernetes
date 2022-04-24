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

package app.simplecloud.simplecloud.database.mongo.start.codec

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import org.bson.BsonInvalidOperationException
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import java.util.*

/**
 * Date: 21.03.22
 * Time: 18:09
 * @author Frederick Baier
 *
 */
class PermissionPlayerConfigurationCodec(
    private val permissionConfigurationCodec: PermissionConfigurationCodec
) : Codec<PermissionPlayerConfiguration> {

    override fun encode(writer: BsonWriter, value: PermissionPlayerConfiguration, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        writer.writeString("uniqueId", value.uniqueId.toString())
        writer.writeStartArray("permissions")
        value.permissions.forEach { permissionConfiguration ->
            this.permissionConfigurationCodec.encode(writer, permissionConfiguration, encoderContext)
        }
        writer.writeEndArray()
        writer.writeEndDocument()
    }

    override fun getEncoderClass(): Class<PermissionPlayerConfiguration> {
        return PermissionPlayerConfiguration::class.java
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): PermissionPlayerConfiguration {
        reader.readStartDocument()
        val uniqueId = UUID.fromString(reader.readString("uniqueId"))
        reader.readName("permissions")
        reader.readStartArray()
        val permissions = ArrayList<PermissionConfiguration>()
        while (true) {
            try {
                permissions.add(this.permissionConfigurationCodec.decode(reader, decoderContext))
            } catch (e: BsonInvalidOperationException) {
                break
            }
        }
        reader.readEndArray()
        reader.readEndDocument()
        return PermissionPlayerConfiguration(uniqueId, permissions)
    }
}