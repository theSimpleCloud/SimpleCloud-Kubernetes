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