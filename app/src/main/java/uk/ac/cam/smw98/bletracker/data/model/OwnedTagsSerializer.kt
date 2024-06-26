package uk.ac.cam.smw98.bletracker.data.model

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import uk.ac.cam.smw98.bletracker.data.ProtoOwnedEntries
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object OwnedTagsSerializer: Serializer<ProtoOwnedEntries> {
    override val defaultValue:  ProtoOwnedEntries = ProtoOwnedEntries.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): ProtoOwnedEntries {
        try {
            return ProtoOwnedEntries.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read OwnedEntries", exception)
        }
    }
    override suspend fun writeTo(t: ProtoOwnedEntries, output: OutputStream)
    {t.writeTo(output)}
}
