package app.simplecloud.simplecloud.node.startup.task.mongo.codec

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