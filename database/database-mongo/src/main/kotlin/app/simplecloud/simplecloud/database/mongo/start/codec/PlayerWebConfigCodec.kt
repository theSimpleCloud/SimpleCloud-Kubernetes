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

import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

/**
 * Date: 21.03.22
 * Time: 18:09
 * @author Frederick Baier
 *
 */
class PlayerWebConfigCodec : Codec<PlayerWebConfig> {

    override fun encode(writer: BsonWriter, value: PlayerWebConfig, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        writer.writeString("password", value.password)
        writer.writeBoolean("hasAccess", value.hasAccess)
        writer.writeEndDocument()
    }

    override fun getEncoderClass(): Class<PlayerWebConfig> {
        return PlayerWebConfig::class.java
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): PlayerWebConfig {
        reader.readStartDocument()
        val password = reader.readString("password")
        val hasAccess = reader.readBoolean("hasAccess")
        reader.readEndDocument()
        return PlayerWebConfig(password, hasAccess)
    }
}