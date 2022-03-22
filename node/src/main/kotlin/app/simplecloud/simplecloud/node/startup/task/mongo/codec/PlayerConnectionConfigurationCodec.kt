package app.simplecloud.simplecloud.node.startup.task.mongo.codec

import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
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
class PlayerConnectionConfigurationCodec(
    private val addressCodec: AddressCodec
) : Codec<PlayerConnectionConfiguration> {

    override fun encode(writer: BsonWriter, value: PlayerConnectionConfiguration, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        writer.writeString("uniqueId", value.uniqueId.toString())
        writer.writeInt32("numericalClientVersion", value.numericalClientVersion)
        writer.writeString("name", value.name)
        writer.writeName("address")
        addressCodec.encode(writer, value.address, encoderContext)
        writer.writeBoolean("onlineMode", value.onlineMode)
        writer.writeEndDocument()
    }

    override fun getEncoderClass(): Class<PlayerConnectionConfiguration> {
        return PlayerConnectionConfiguration::class.java
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): PlayerConnectionConfiguration {
        reader.readStartDocument()
        val uniqueId = UUID.fromString(reader.readString("uniqueId"))
        val numericalClientVersion = reader.readInt32()
        val name = reader.readString("name")
        reader.readName("address")
        val address = addressCodec.decode(reader, decoderContext)
        val onlineMode = reader.readBoolean("onlineMode")
        reader.readEndDocument()
        return PlayerConnectionConfiguration(
            uniqueId,
            numericalClientVersion,
            name,
            address,
            onlineMode
        )
    }
}