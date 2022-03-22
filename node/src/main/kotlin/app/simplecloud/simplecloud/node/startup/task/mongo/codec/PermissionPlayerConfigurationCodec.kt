package app.simplecloud.simplecloud.node.startup.task.mongo.codec

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