package io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber


class MultipleChoiceConverter {
    private val gson = Gson()

    @TypeConverter
    fun multipleChoiceToJson(multipleChoice: List<String>?): String {
        if (multipleChoice == null) return ""
        return try {
            gson.toJson(multipleChoice, object : TypeToken<List<String>?>() {}.type)
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert $multipleChoice")
            ""
        }
    }

    @TypeConverter
    fun jsonToMultipleChoice(json: String): List<String> {
        return try {
            gson.fromJson(json, object : TypeToken<List<String>?>() {}.type)
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert json to multiple choice")
            return emptyList()
        }
    }
}