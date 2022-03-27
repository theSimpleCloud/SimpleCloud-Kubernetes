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

package app.simplecloud.simplecloud.node.startup.task.mongo.codec

import app.simplecloud.simplecloud.api.permission.configuration.PermissionConfiguration
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

class PermissionConfigurationCodec : Codec<PermissionConfiguration> {

    override fun encode(writer: BsonWriter, value: PermissionConfiguration, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        writer.writeString("permissionString", value.permissionString)
        writer.writeBoolean("active", value.active)
        writer.writeInt64("expiresAtTimestamp", value.expiresAtTimestamp)
        if (value.targetProcessGroup == null) {
            writer.writeNull("targetProcessGroup")
        } else {
            writer.writeString("targetProcessGroup", value.targetProcessGroup)
        }
        writer.writeEndDocument()
    }

    override fun getEncoderClass(): Class<PermissionConfiguration> {
        return PermissionConfiguration::class.java
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): PermissionConfiguration {
        reader.readStartDocument()
        val permissionString = reader.readString("permissionString")
        val active = reader.readBoolean("active")
        val expiresAtTimestamp = reader.readInt64("expiresAtTimestamp")
        val mark = reader.mark
        val bsonType = reader.readBsonType()
        mark.reset()
        val targetProcessGroup = if (bsonType == BsonType.NULL) {
            reader.readNull("targetProcessGroup")
            null
        } else {
            reader.readString("targetProcessGroup")
        }
        reader.readEndDocument()
        return PermissionConfiguration(permissionString, active, expiresAtTimestamp, targetProcessGroup)
    }

}
