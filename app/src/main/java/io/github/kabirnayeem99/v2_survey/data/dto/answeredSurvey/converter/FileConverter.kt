package io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.converter

import androidx.room.TypeConverter
import timber.log.Timber
import java.io.File

class FileConverter {

    @TypeConverter
    fun fileToJson(file: File?): String {
        return try {
            if (file == null) return ""
            file.path
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert to JSON")
            ""
        }
    }

    @TypeConverter
    fun jsonToFile(path: String): File {
        return try {
            File(path)
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert to JSON")
            File("")
        }
    }
}