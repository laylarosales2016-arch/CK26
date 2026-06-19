package com.sam.myapplication.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus): String {
        return status.name
    }

    @TypeConverter
    fun toAttendanceStatus(status: String): AttendanceStatus {
        return AttendanceStatus.valueOf(status)
    }

    @TypeConverter
    fun fromCustomOffenceList(value: List<CustomOffence>?): String {
        return Json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toCustomOffenceList(value: String): List<CustomOffence>? {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return Json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toStringList(value: String): List<String>? {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
