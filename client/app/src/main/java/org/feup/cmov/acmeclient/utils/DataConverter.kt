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

    @TypeConverter
    fun jsonToMap(value: String): Map<String, Int> {
        val type = object : TypeToken<Map<String, Int>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun mapToJson(value: Map<String, Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun listToJson(value: List<String>): String {
        return Gson().toJson(value)
    }
}