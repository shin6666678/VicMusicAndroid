package com.shin.vicmusic.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object UserPrefsSerializer : Serializer<UserPrefsProto> {
    override val defaultValue: UserPrefsProto = UserPrefsProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPrefsProto {
        try {
            return UserPrefsProto.parseFrom(input) // this is an auto-generated method
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPrefsProto, output: OutputStream) {
        withContext(Dispatchers.IO) {
            t.writeTo(output)
        }
    }
}
