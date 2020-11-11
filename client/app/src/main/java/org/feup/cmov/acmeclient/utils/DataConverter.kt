package org.feup.cmov.acmeclient.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverter {
    @TypeConverter
    fun jsonToSet(value: String): Set<String> {
        val type = object : TypeToken<Set<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun setToJson(value: Set<String>): String {
        return Gson().toJson(value)
    }
}