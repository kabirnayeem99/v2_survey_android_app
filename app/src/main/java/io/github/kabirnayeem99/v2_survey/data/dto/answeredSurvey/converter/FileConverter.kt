package io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import timber.log.Timber
import java.io.File

class FileConverter {
    private val gson = Gson()

    @TypeConverter
    fun fileToJson(file: File?): String {
        return try {
            if (file == null) return ""
            gson.toJson(file)
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert to JSON")
            ""
        }
    }

    @TypeConverter
    fun jsonToFile(json: String): File {
        return try {
            gson.fromJson(json, File::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert to JSON")
            File("")
        }
    }
}