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
