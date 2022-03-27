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

import app.simplecloud.simplecloud.api.utils.Address
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

/**
 * Date: 21.03.22
 * Time: 18:36
 * @author Frederick Baier
 *
 */
class AddressCodec : Codec<Address> {

    override fun encode(writer: BsonWriter, value: Address, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        writer.writeString("host", value.host)
        writer.writeInt32("port", value.port)
        writer.writeEndDocument()
    }

    override fun getEncoderClass(): Class<Address> {
        return Address::class.java
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): Address {
        reader.readStartDocument()
        val host = reader.readString("host")
        val port = reader.readInt32("port")
        reader.readEndDocument()
        return Address(host, port)
    }
}